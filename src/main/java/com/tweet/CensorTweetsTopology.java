package com.crypto;

import com.crypto.model.Tweet;
import com.crypto.serdes.TweetSerdes;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.Map;


@Slf4j
public class CensorTweetsTopology {

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        TweetSerdes tweetSerdes = new TweetSerdes();
        Predicate<byte[], Tweet> englishTweets = (key, tweet) -> tweet.getLang().equals("en");
        Predicate<byte[], Tweet> nonEnglishTweets = (key, tweet) -> !tweet.getLang().equals("en");

        KStream<byte[], Tweet> tweetKStream = streamsBuilder.stream("tweets", Consumed.with(Serdes.ByteArray(), tweetSerdes));
        tweetKStream.print(Printed.<byte[], Tweet>toSysOut().withLabel("tweets-stream"));

        // retweet이 아닌 경우만 담기
        KStream<byte[], Tweet> filteredKStream = tweetKStream.filterNot((key, tweet) -> tweet.isRetweet());

        // tweet이 영어인 경우와 영어가 아닌 경우 나누기
        Map<String, KStream<byte[], Tweet>> branchStreamMap = filteredKStream.split(Named.as("language-"))
                .branch(englishTweets, Branched.as("english"))
                .branch(nonEnglishTweets, Branched.as("nonEnglish"))
                .noDefaultBranch();

        // non-English tweets를 영어로 번역하는 프로세서
        KStream<byte[], Tweet> translatedStream = branchStreamMap.get("language-nonEnglish")
                .mapValues((tweet) -> translateToEnglish(tweet, "en"));

        // englishTweets, nonEnglishTweets 두 노드를 다시 병합
        KStream<byte[], Tweet> mergedStream = branchStreamMap.get("language-english").merge(translatedStream);

        // 부적절한 tweeet에 경고 추가
        KStream<byte[], Tweet> mappedStream = mergedStream.mapValues(tweet -> {
            if (tweet.getText().contains("shit")) {
                int totalWarning = tweet.getWarning() + 1;
                tweet.setWarning(totalWarning);
            }
            return tweet;
        });

        mappedStream.to("censored-topic", Produced.with(Serdes.ByteArray(), tweetSerdes));

        return streamsBuilder.build();
    }

    private static Tweet translateToEnglish(Tweet tweet, String en) {
        // LibreTranslate API or Google Cloud Translation API 활용 추천
        return Tweet.translateToEng(tweet, "translated to English");
    }
}
