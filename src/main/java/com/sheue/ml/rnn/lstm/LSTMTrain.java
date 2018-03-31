package com.sheue.ml.rnn.lstm;

import com.sheue.ml.dataset.CharText;
import com.sheue.ml.layers.MatIniter;
import com.sheue.ml.utils.LossFunction;
import org.jblas.DoubleMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Language Model using LSTM
public class LSTMTrain {
    private LSTM lstm;

    public LSTMTrain(int inSize, int outSize, MatIniter initer) {
        lstm = new LSTM(inSize, outSize, initer);
    }

    private void train(CharText ctext, double lr) {
        Map<Integer, String> indexChar = ctext.getIndexChar();
        Map<String, DoubleMatrix> charVector = ctext.getCharVector();
        List<String> sequence = ctext.getSequence();

        for (int i = 0; i < 1000; i++) {
            double error = 0;
            double num = 0;
            double start = System.currentTimeMillis();
            for (String seq : sequence) {
                Map<String, DoubleMatrix> acts = new HashMap<>();
                System.out.print("start:" + String.valueOf(seq.charAt(0)) + "--");
                for (int t = 0; t < seq.length() - 1; t++) {
                    DoubleMatrix xt = charVector.get(String.valueOf(seq.charAt(t)));
                    acts.put("x" + t, xt);

                    lstm.active(t, acts);

                    DoubleMatrix predcitYt = lstm.decode(acts.get("h" + t));
                    acts.put("py" + t, predcitYt);
                    DoubleMatrix trueYt = charVector.get(String.valueOf(seq.charAt(t + 1)));
                    acts.put("y" + t, trueYt);

                    System.out.print(indexChar.get(predcitYt.argmax()));
                    error += LossFunction.getMeanCategoricalCrossEntropy(predcitYt, trueYt);

                }

                System.out.println();
                lstm.bptt(acts, seq.length() - 2, lr);

                num += seq.length();
            }
//            System.out.println("Iter = " + i + ", error = " + error / num + ", time = " + (System.currentTimeMillis() - start) / 1000 + "s");
            System.out.println("Iter=" + i + "error=" + error + ",num=" + num);
            if (error / num < 2) {
                System.out.println("模型训练完成！");
                break;
            }
        }
    }

    public static void main(String[] args) {
        int hiddenSize = 100;
        double lr = 1;
        CharText ct = new CharText();
        LSTMTrain lstm = new LSTMTrain(ct.getCharIndex().size(), hiddenSize, new MatIniter(MatIniter.Type.Uniform, 0.1, 0, 0));
        lstm.train(ct, lr);
    }

}
