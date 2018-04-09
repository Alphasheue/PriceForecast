package com.sheue.ml.dataset;

import com.sheue.app.bean.Data;
import com.sheue.app.dao.PriceDAO;
import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataText {

    private Map<String, Integer> charIndex = new HashMap<>();
    private Map<Integer, String> indexChar = new HashMap<>();
    private Map<String, DoubleMatrix> charVector = new HashMap<>();
    private List<String> sequence = new ArrayList<>();

    public DataText(String name) {
        List<Data> list = PriceDAO.getTrain(name);
        for (Data data : list) {
            sequence.add(String.valueOf(data.getPrice()));
            String key = String.valueOf(data.getPrice());
            if (!charIndex.containsKey(key)) {
                charIndex.put(key, charIndex.size());
                indexChar.put(charIndex.get(key), key);
            }
        }
        buildDistributedRepresentations();
    }

    private void buildDistributedRepresentations() {
        for (String c : charIndex.keySet()) {
            DoubleMatrix xt = DoubleMatrix.zeros(1, charIndex.size());
            xt.put(charIndex.get(c), 1);
            charVector.put(c, xt);
        }
    }

    public Map<String, Integer> getCharIndex() {
        return charIndex;
    }

    public Map<String, DoubleMatrix> getCharVector() {
        return charVector;
    }

    public List<String> getSequence() {
        return sequence;
    }

    public Map<Integer, String> getIndexChar() {
        return indexChar;
    }

}
