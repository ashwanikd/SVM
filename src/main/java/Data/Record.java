package Data;

/**
 * @author ashwani kumar dwivedi
 * @version 1.0
 */

public class Record {
    /**
     * an array of data
     */
    public Cell[] data;

    /**
     * stores the number of columns
     */
    public int N;
    Record(int numberofcolumns){
        N = numberofcolumns;
        data = new Cell[N];
    }

    /**
     * for setting the value
     * @param index position
     * @param value value to put
     */
    void set(int index,String value){
        data[index] = new Cell(value);
    }
    void set(int index,int value){
        data[index] = new Cell(value);
    }
    void set(int index,double value){
        data[index] = new Cell(value);
    }
    void set(int index,float value){
        data[index] = new Cell(value);
    }
    Cell get(int column){
        return data[column];
    }
}
