/**
  * Created by smukherjee5 on 12/5/17.
  */

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter._
import org.elasticsearch.spark._
import java.text.SimpleDateFormat;
import java.util.Locale;


object TwitterSentimentAnalysis {
  def main(args: Array[String]) {
//    if (args.length < 4) {
//      System.err.println("Usage: TwitterPopularTags <consumer key> <consumer secret> " + "<access token> <access token secret> [<filters>]")
//      System.exit(1)
//    }

  //Please update these with your twitter API keys, these are sample values
  val consumerKey="kcRf6SOLPuMCyibJELsmdTS8D"
  val consumerSecret="54CQlbZWLvD7Vuw2ZmMhIDeQ49MqMPXGxiRbNp0brtMDWz2zS8"
  val accessToken="35808111-q3MvekUCy85PzDEdPlq5pbwkeBjImHpFmTlwCKkvH"
  val accessTokenSecret="j3cAQzOOSm7jlYO6mM4lrHrvglbPLc4jIiZZYopyXmjSp"

    //StreamingExamples.setStreamingLogLevels()
    //Passing our Twitter keys and tokens as arguments for authorization
    //val Array(consumerKey, consumerSecret, accessToken, accessTokenSecret) = args.take(4)
    //val filters = args.takeRight(args.length - 4)

    // Set the system properties so that Twitter4j library used by twitter stream
    // Use them to generate OAuth credentials
    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)

    val sparkConf = new SparkConf().setAppName("twitterSentiment").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(2))


    sparkConf.set("es.resource","sent_spark_provider_twitter/tweet")
    sparkConf.set("es.index.auto.create", "true")
    // Define the location of Elasticsearch cluster
    sparkConf.set("es.nodes", "localhost")
    sparkConf.set("es.port", "9200")

    val stream = TwitterUtils.createStream(ssc, None)

    stream.print()

    var esconf = Map("es.nodes" -> "localhost", "es.port" -> "9200")

    val dateFormat = new SimpleDateFormat(
      "EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
    dateFormat.setLenient(true);

    val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
        val tweets = stream.filter {t =>
            val tags = t.getText.split(" ").filter(_.startsWith("#")).map(_.toLowerCase)
            tags.contains("smallbusiness")
            //&& tags.contains("#blockchain") && tags.contains("#bitcoin") && tags.contains("#ethereum")
            //||tags.contains("bitcoin") || tags.contains("blockchain") && tags.contains("ethereum")
          }

    //stream.filter(tweet => tweet.getGeoLocation != null)

    //tweets
      stream
      .foreachRDD{
        rdd =>
          rdd
            .map(t => {
          Map(
          "user"-> t.getUser.getScreenName,
          //"created_at" -> formatter.format(dateFormat.parse(t.getCreatedAt.toString)),//formatter.format(t.getUser.getCreatedAt.getTime),
          "created" -> t.getCreatedAt.getTime.toString,
          "location" -> Option(t.getGeoLocation).map(geo => { s"${geo.getLatitude},${geo.getLongitude}" }),
          "user_location" -> t.getUser.getLocation,
          "text" -> t.getText,
          "hashtags" -> t.getHashtagEntities.map(_.getText),
          "retweet" -> t.getRetweetCount,
          "language" -> t.getLang.toString(),
          "sentiment" -> NLPUtils.analyzeSentiment(t.getText).toString
        )
      }

      )
          .saveToEs("sent_spark_provider_twitter/tweet",esconf)
    }




    ssc.start()
    ssc.awaitTermination()

    ssc.start()
    ssc.awaitTermination()
  }
}
