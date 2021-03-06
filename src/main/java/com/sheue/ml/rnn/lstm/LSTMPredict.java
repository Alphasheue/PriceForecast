package com.sheue.ml.rnn.lstm;

import com.sheue.app.bean.Data;
import com.sheue.app.dao.PriceDAO;
import com.sheue.ml.dataset.DataText;
import com.sheue.ml.layers.MatIniter;
import com.sheue.ml.utils.LossFunction;
import org.jblas.DoubleMatrix;
import org.jfree.data.xy.DefaultXYDataset;

import java.util.*;

// Language Model using LSTM
public class LSTMPredict {
    private LSTM lstm;
    private DataText dataText;
    private DefaultXYDataset dataset;
    private double accuracy;

    public LSTMPredict() {

    }

    public LSTMPredict(int inSize, int outSize, MatIniter initer) {
        this.lstm = new LSTM(inSize, outSize, initer);
    }

    public LSTMPredict init(String itemName, double lr, double acc, DefaultXYDataset dataset) {
        DataText dt = new DataText(itemName);
        int hiddenSize = 100;
        LSTMPredict lstm = new LSTMPredict(dt.getCharIndex().size(), hiddenSize, new MatIniter(MatIniter.Type.Uniform, 0.1, 0, 0));
        lstm.dataText = dt;
        lstm.dataset = dataset;
        System.out.println("LSTM模型初始化成功！");
        lstm.train(itemName, lr, acc);
        return lstm;
    }

    private void train(String itemName, double lr, double acc) {
        Map<Integer, String> indexChar = dataText.getIndexChar();
        Map<String, DoubleMatrix> charVector = dataText.getCharVector();
        Map<String, Integer> charIndex = dataText.getCharIndex();
        List<String> sequenceList = dataText.getSequence();
        int totalTrain = 200;
//        double[][] trainSet = new double[2][totalTrain];
        for (int i = 0; i < totalTrain; i++) {
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
//            trainSet[0][i] = i + 1;
//            trainSet[1][i] = error;

            lstm.bptt(acts, sequenceList.size() - 2, lr);

            System.out.println("第" + (i + 1) + "次训练，误差=" + error + "，训练用例数=" + (num - 1) + "，预测错误数=" + wrong);
            if (wrong / num < acc) {
                System.out.println("模型训练完成！");
                break;
            }
            if (i == totalTrain - 1) {
                System.out.println("已达到训练次数上限" + (i + 1) + "次！");
            }
        }
//        dataset.addSeries("LSTM", trainSet);

        System.out.println("开始测试：");
        List<Data> list = PriceDAO.getTest(itemName, 61);
        Collections.reverse(list);
        double error = 0;
        double num = list.size() - 1;
        double wrong = 0;
        double[][] predictSet = new double[2][list.size() - 1];
        double[][] realSet = new double[2][list.size() - 1];
        Map<String, DoubleMatrix> acts = new HashMap<>();
        for (int t = 0; t < num; t++) {
            String sequence = String.valueOf(list.get(t).getPrice());
            sequence = indexChar.get(convertSequence(charIndex, sequence));
            DoubleMatrix xt = charVector.get(sequence);
            acts.put("x" + t, xt);
            lstm.active(t, acts);
            DoubleMatrix predcitYt = lstm.decode(acts.get("h" + t));
            acts.put("py" + t, predcitYt);
            String nextSequence = indexChar.get(convertSequence(charIndex, String.valueOf(list.get(t + 1).getPrice())));
            DoubleMatrix trueYt = charVector.get(nextSequence);
            acts.put("y" + t, trueYt);

            error += LossFunction.getMeanCategoricalCrossEntropy(predcitYt, trueYt);

            double predict = Double.parseDouble(indexChar.get(predcitYt.argmax()));
            double real = Double.parseDouble(indexChar.get(trueYt.argmax()));
//            System.out.println("iter" + (t + 1) + ",predict=" + predict + ",real=" + real);
            predictSet[0][t] = t + 1;
            predictSet[1][t] = predict;
            realSet[0][t] = t + 1;
            realSet[1][t] = real;
            if (Math.abs(predict - real) > 0.2) {
                wrong++;
            }
        }

        dataset.addSeries("LSTM预测价格", predictSet);
        dataset.addSeries("实际价格", realSet);

        this.accuracy = (1 - wrong / num) * 100;
        System.out.println("误差=" + error + "，测试用例数=" + num + "，预测错误数=" + wrong + "，准确率"
                + String.format("%.2f", this.accuracy) + "%");
    }

    public List<String> predict(String itemName, int time) {
        Map<Integer, String> indexChar = dataText.getIndexChar();
        Map<String, DoubleMatrix> charVector = dataText.getCharVector();
        Map<String, Integer> charIndex = dataText.getCharIndex();
        List<String> result = new ArrayList<>();

        List<Data> list = PriceDAO.getPage(itemName, 1);
        String sequence = String.valueOf(list.get(0).getPrice());
        Integer index = convertSequence(charIndex, sequence);
        sequence = indexChar.get(index);
        Map<String, DoubleMatrix> acts = new HashMap<>();
        for (int t = 0; t < time; t++) {
            DoubleMatrix xt = charVector.get(sequence);
            acts.put("x" + t, xt);
            lstm.active(t, acts);
            DoubleMatrix predcitYt = lstm.decode(acts.get("h" + t));
            acts.put("py" + t, predcitYt);
            index = (index + 1) % indexChar.size();
            sequence = indexChar.get(index);
            DoubleMatrix trueYt = charVector.get(sequence);
            acts.put("y" + t, trueYt);

            result.add(indexChar.get(predcitYt.argmax()));
        }
        return result;
    }

    private Integer convertSequence(Map<String, Integer> charIndex, String sequence) {
        if (charIndex.containsKey(sequence)) {
            return charIndex.get(sequence);
        } else {
            for (int i = 1; i <= 5; i++) {
                String sequence2 = String.valueOf(Double.parseDouble(sequence) - 0.1 * i);
                if (charIndex.containsKey(sequence2)) {
                    return charIndex.get(sequence2);
                } else {
                    sequence2 = String.valueOf(Double.parseDouble(sequence) + 0.1 * i);
                    if (charIndex.containsKey(sequence2)) {
                        return charIndex.get(sequence2);
                    }
                }
            }
        }
        System.out.println("convert sequence failed:seq=" + sequence);
        return null;
    }

    public LSTM getLstm() {
        return lstm;
    }

    public DefaultXYDataset getDataset() {
        return dataset;
    }

    public DataText getDataText() {
        return dataText;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public static void main(String[] args) {
        String itemName = "苹果";
        double lr = 1;      //学习速率，越高模型训练速度越快，准度越低
        double acc = 0.33;   // 错误率，越高越容易训练出来
        DefaultXYDataset dataset = new DefaultXYDataset();
        LSTMPredict lstm = new LSTMPredict().init(itemName, lr, acc, dataset);
        List<String> list = lstm.predict(itemName, 10);
        for (String res : list) {
            System.out.println("res:" + res);
        }
    }

}
