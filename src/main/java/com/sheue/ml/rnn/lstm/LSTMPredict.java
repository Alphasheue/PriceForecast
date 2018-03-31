package com.sheue.ml.rnn.lstm;

import com.sheue.app.bean.Data;
import com.sheue.app.dao.PriceDAO;
import com.sheue.app.utils.LineChartUtil;
import com.sheue.ml.dataset.DataText;
import com.sheue.ml.layers.MatIniter;
import com.sheue.ml.utils.LossFunction;
import org.jblas.DoubleMatrix;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Language Model using LSTM
public class LSTMPredict {
    private LSTM lstm;
    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public LSTMPredict(int inSize, int outSize, MatIniter initer) {
        lstm = new LSTM(inSize, outSize, initer);
    }

    private void train(DataText dataText, double lr, double acc) {
        Map<Integer, String> indexChar = dataText.getIndexChar();
        Map<String, DoubleMatrix> charVector = dataText.getCharVector();
        Map<String, Integer> charIndex = dataText.getCharIndex();
        List<String> sequenceList = dataText.getSequence();
        for (int i = 0; i < 600; i++) {
            double error = 0;
            double num = sequenceList.size();
            double wrong = 0;
            Map<String, DoubleMatrix> acts = new HashMap<>();
            List<String> predict = new ArrayList<>();
            for (int t = 0; t < sequenceList.size() - 1; t++) {
                String sequence = sequenceList.get(t);
//                System.out.print("real:" + sequence);
                DoubleMatrix xt = charVector.get(sequence);
                acts.put("x" + t, xt);

                lstm.active(t, acts);

                DoubleMatrix predcitYt = lstm.decode(acts.get("h" + t));
                acts.put("py" + t, predcitYt);
                DoubleMatrix trueYt = charVector.get(sequenceList.get(t + 1));
                acts.put("y" + t, trueYt);

//                System.out.println("--predict:" + indexChar.get(predcitYt.argmax()));
                error += LossFunction.getMeanCategoricalCrossEntropy(predcitYt, trueYt);
                predict.add(indexChar.get(predcitYt.argmax()));

            }

            for (int j = 0; j < sequenceList.size() - 1; j++) {
                if (sequenceList.size() - 1 != predict.size()) {
                    System.out.println("error at " + j);
                    break;
                }
                if (!sequenceList.get(j).equals(predict.get(j))) {
                    wrong++;
                }
            }

            lstm.bptt(acts, sequenceList.size() - 2, lr);

            System.out.println("Iter=" + i + ",error=" + error + ",num=" + num + ",wrong=" + wrong);
            if (wrong / num < acc) {
                System.out.println("模型训练完成！");
                break;
            }
        }

        // 开始测试
        List<Data> list = PriceDAO.getTest("苹果");
        double error = 0;
        double num = list.size();
        double wrong = 0;
        int newData = 0;
        Map<String, DoubleMatrix> acts = new HashMap<>();
        for (int t = 0; t < list.size() - 1; t++) {
            String sequence = String.valueOf(list.get(t).getPrice());
            int index = 0;
            DoubleMatrix xt = null;
            if (charVector.containsKey(sequence)) {
                xt = charVector.get(sequence);
                index = charIndex.get(sequence);
            } else {
                newData++;
                for (int j = 1; j <= 5; j++) {
                    String sequence2 = String.valueOf(list.get(t).getPrice() - 0.1 * j);
                    if (charVector.containsKey(sequence2)) {
                        xt = charVector.get(sequence2);
                        sequence = sequence2;
                        index = charIndex.get(sequence);
                        System.out.println("newseq:" + "-0." + j);
                        break;
                    } else {
                        sequence2 = String.valueOf(list.get(t).getPrice() + 0.1 * j);
                        if (charVector.containsKey(sequence2)) {
                            xt = charVector.get(sequence2);
                            sequence = sequence2;
                            index = charIndex.get(sequence);
                            System.out.println("newseq:" + "+0." + j);
                            break;
                        }
                    }
                }
                if (xt == null) {
                    System.out.println("此数无解：t=" + t + ",seq:" + sequence);
                    continue;
                }
            }
            acts.put("x" + t, xt);
            lstm.active(t, acts);
            DoubleMatrix predcitYt = lstm.decode(acts.get("h" + t));
            acts.put("py" + t, predcitYt);
            DoubleMatrix trueYt = charVector.get(sequence);
            acts.put("y" + t, trueYt);

            double predict = Double.parseDouble(indexChar.get(predcitYt.argmax()));
            double trueY = Double.parseDouble(indexChar.get(trueYt.argmax()));
            double real = list.get(t + 1).getPrice();
            System.out.println("iter" + t + ",predict=" + predict + ",true=" + trueY + ",real=" + real);
            dataset.addValue(predict, "predict", String.valueOf(t));
            dataset.addValue(trueY, "true", String.valueOf(t));
            dataset.addValue(real, "real", String.valueOf(t));
            if (predict != trueY) {
                error++;
            }
            if (Math.abs(predict - real) > 0.3) {
                wrong++;
            }
        }
        JFreeChart jFreeChart = LineChartUtil.createLineChart("错误率", "次数", "大小", dataset);
        LineChartUtil.draw("./data/LSTMResult.jpg", jFreeChart, 1280, 720);

        System.out.println("error=" + error + ",num=" + num + ",wrong=" + wrong);
        System.out.println("new:" + newData);
    }

    public static void main(String[] args) {
        DataText dt = new DataText("苹果");
        double lr = 0.9;      //学习速率，越高模型训练速度越快，准度越低
        double acc = 0.5;   // 要求模型拥有的最高错误率
        int hiddenSize = 100;
        LSTMPredict lstm = new LSTMPredict(dt.getCharIndex().size(), hiddenSize, new MatIniter(MatIniter.Type.Uniform, 0.1, 0, 0));
        lstm.train(dt, lr, acc);
    }

}
