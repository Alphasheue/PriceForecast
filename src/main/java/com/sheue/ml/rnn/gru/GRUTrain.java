package com.sheue.ml.rnn.gru;

import com.sheue.ml.dataset.CharText;
import com.sheue.ml.layers.MatIniter;
import com.sheue.ml.utils.LossFunction;
import org.jblas.DoubleMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Language Model using GRU
public class GRUTrain {
    private GRU gru;

    public GRUTrain(int inSize, int outSize, MatIniter initer) {
        this.gru = new GRU(inSize, outSize, initer);
    }

    private void train(CharText charText, double lr) {
        Map<Integer, String> indexChar = charText.getIndexChar();
        Map<String, DoubleMatrix> charVector = charText.getCharVector();
        List<String> sequenceList = charText.getSequence();
        for (int i = 0; i < 1000; i++) {
            double error = 0;
            double num = 0;
            double wrong = 0;
            for (String sequence : sequenceList) {
                StringBuilder predict = new StringBuilder(String.valueOf(sequence.charAt(0)));
                Map<String, DoubleMatrix> acts = new HashMap<>();
                System.out.print("start:" + String.valueOf(sequence.charAt(0)) + "--");
                for (int t = 0; t < (sequence.length() - 1); t++) {
                    DoubleMatrix xt;
                    xt = charVector.get(String.valueOf(sequence.charAt(t)));
                    acts.put("x" + t, xt);

                    gru.active(t, acts);

                    DoubleMatrix predcitYt = gru.decode(acts.get("h" + t));
                    acts.put("py" + t, predcitYt);
                    DoubleMatrix trueYt = charVector.get(String.valueOf(sequence.charAt(t + 1)));
                    acts.put("y" + t, trueYt);

                    System.out.print(indexChar.get(predcitYt.argmax()));
                    predict.append(indexChar.get(predcitYt.argmax()));
                    error += LossFunction.getMeanCategoricalCrossEntropy(predcitYt, trueYt);

                }
                System.out.println();

                for (int j = 0; j < sequence.length(); j++) {
                    String pdt = predict.toString();
                    if (sequence.length() != pdt.length()) {
                        System.out.println("error at " + j);
                        break;
                    }
                    if (sequence.charAt(j) != pdt.charAt(j)) {
                        wrong++;
                    }
                }

                gru.bptt(acts, sequence.length() - 2, lr);

                num += sequence.length();
            }

            System.out.println("Iter=" + i + "error=" + error + ",num=" + num + ",wrong=" + wrong);
            if (error / num < 2) {
                System.out.println("模型训练完成！");
                break;
            }
        }
    }

    public GRU getGru() {
        return gru;
    }

    public static void main(String[] args) {
        CharText ct = new CharText();
        double lr = 1;    //学习速率，越高模型训练速度越快
        int hiddenSize = 100;
        GRUTrain gru = new GRUTrain(ct.getCharIndex().size(), hiddenSize, new MatIniter(MatIniter.Type.Uniform, 0.1, 0, 0));
        gru.train(ct, lr);
    }

}
