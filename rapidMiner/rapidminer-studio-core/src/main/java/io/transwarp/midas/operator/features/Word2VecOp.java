package io.transwarp.midas.operator.features;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.features.Word2VecParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by endy on 16-8-4.
 */
public class Word2VecOp extends BaseOp {
    public Word2VecOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private InputPort input = getInputPorts().createPort("input");
    private OutputPort output = getOutputPorts().createPort("output");
    private OutputPort model = getOutputPorts().createPort("model");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeInt(Word2VecParams.VectorSize(),
                "The dimension of codes after transforming from words ",
                0, Integer.MAX_VALUE, 100,false);
        types.add(type);

        type = new ParameterTypeDouble(Word2VecParams.StepSize(),
                "Step size to be used for each iteration of optimization",
                0, Double.MAX_VALUE, 0.025 ,false);
        types.add(type);

        type = new ParameterTypeInt(Word2VecParams.MaxIter(),
                "Maximum number of iterations",
                0, Integer.MAX_VALUE, 1 ,false);
        types.add(type);

        type = new ParameterTypeInt(Word2VecParams.MinCount(),
                "The minimum number of times a token must appear to be included in the word2vec model's vocabulary",
                0, Integer.MAX_VALUE, 5 ,false);
        types.add(type);

        type = new ParameterTypeInt(Word2VecParams.NumPartitions(),
                "The number of partitions for sentences of words",
                0, Integer.MAX_VALUE, 1 ,false);
        types.add(type);

        type = new ParameterTypeInt(Word2VecParams.WindowSize(), "Sets the window of words",
                0, Integer.MAX_VALUE, 5 ,false);
        types.add(type);

        type = new ParameterTypeInt(Word2VecParams.MaxSentenceLength(),
                "Sets the maximum length (in words) of each sentence in the input data.\n" +
                "   * Any sentence longer than this threshold will be divided into chunks of\n" +
                "   * up to `maxSentenceLength` size",
                0, Integer.MAX_VALUE, 1000 ,false);
        types.add(type);

        type = new ParameterTypeInt(Word2VecParams.CBOW(), "Use continues bag-of-words model",
                0, 1, 0 ,false);
        types.add(type);

        type = new ParameterTypeDouble( Word2VecParams.Sample(),
                "Use sub-sampling trick to improve the performance",
                0, 1, 0 ,false);
        types.add(type);

        type = new ParameterTypeInt(Word2VecParams.HS(),
                "Use hierarchical softmax method to train the model",
                0, 1, 1 ,false);
        types.add(type);

        type = new ParameterTypeInt(Word2VecParams.Negative(),
                "Use negative sampling method to train the model",
                0, 1, 0 ,false);
        types.add(type);

        return types;
    }

}
