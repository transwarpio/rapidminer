package io.transwarp.midas.constant.midas.params.features

/**
  * Created by linchen on 17-5-9.
  */
object ApplyWord2VecParams {
  val source = "source"
  val inputColumn = "input_column"
  val outputColumn = "output_column"
  val useWordEmbeddingFromInputPort = "use_word_embedding_from_input_port"
  val usePreTrainedWordEmbedding = "use_pre_trained_word_embedding"

  val supportedSources = Array(useWordEmbeddingFromInputPort, usePreTrainedWordEmbedding)
}
