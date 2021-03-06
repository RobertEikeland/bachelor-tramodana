package no.sysco.middleware.tramodana.builder

import com.typesafe.config.ConfigFactory

object AppConfig {
  case class KafkaConfig(bootstrapServers: String)
  case class BuilderServerConfig(name: String, kafkaConfig: KafkaConfig)

  private val builderConfig = ConfigFactory.load().getConfig("builder")
  private val appName: String = builderConfig.getString("name")
  private val serverConfig = builderConfig.getConfig("server")
  private val kafkaBuilderConfig = serverConfig.getConfig("kafka")
  private val kafkaBootstrapServers = kafkaBuilderConfig.getString("bootstrap-servers")

  if (kafkaBootstrapServers == null || kafkaBootstrapServers.isEmpty) {
    System.err.println("Builder Kafka Bootstrap Servers configuration variable must be defined. " +
      "Env variable: `KAFKA_BOOTSTRAP_SERVERS`")
    System.exit(1)
  }

  println(s"Kafka Bootstrap Servers = $kafkaBootstrapServers")

  def buildServerConfig(): BuilderServerConfig = BuilderServerConfig(appName, KafkaConfig(kafkaBootstrapServers))
}
