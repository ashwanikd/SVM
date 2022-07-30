package SupportVectorMachine;

import Data.Record;
import Data.Table;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @author ashwani kumar dwivedi
 * @version 1.0
 */

/**
 * <p>
 *     %%%%%%%%%%%%%%%%%% Support Vector Machine Classifier %%%%%%%%%%%%%%%%%%%%%%%%%%
 *     %                classifier for linearly seperable data                       %
 *     %    the given data must be binary classification for classification to work  %
 *     %            since this is the initial stage of development                   %
 *     %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 * </p>
 */
public class Classifier {
    // stores the data
    Table data;
    // stores the weights of hyperplane
    double[] weight;
    // bias value of hyperplane
    double bias;
    // stores the classes
    LinkedList<Double> classes;
    /**
     * <p>
     *     gradient descent used for optimization of
     *     langrangian function and hence the maximum cost for convergence
     * </p>
     */
    final int MAXIMUMITERATIONS = 50000;
    /**
     * stores the accuracy of model
     */
    double Accuracy;

    /**
     * constructor for model
     * @param database the data used
     */
    public Classifier(Table database){

        classes = new LinkedList<>();

        //copying the data
        System.out.println("Copying data...");
        data = new Table();
        data.records = (LinkedList<Record>) database.records.clone();
        data.columns = (LinkedList<String>) database.columns.clone();
        data.NumberOfColumns = database.NumberOfColumns;
        data.NumberOfRecords = database.NumberOfRecords;

        // initializing weight vector and bias value
        weight = new double[data.NumberOfColumns-1];
        for(int i=0;i<data.NumberOfColumns-1;i++){
            weight[i] = Math.random();
        }
        bias = Math.random();

        //getting classes
        boolean set = false;
        for(int i=0;i<data.records.size();i++){
            if(!set){
                classes.add((double)data.records.get(i).data[data.NumberOfColumns-1].data);
                set = true;
            }
            if(classes.get(0) != (double)data.records.get(i).data[data.NumberOfColumns-1].data){
                classes.add((double)data.records.get(i).data[data.NumberOfColumns-1].data);
                break;
            }
        }
        for(int i=0;i<data.records.size();i++){
            if(classes.get(0) == (double)data.records.get(i).data[data.NumberOfColumns-1].data){
                for(int j=0;j<data.NumberOfColumns;j++){
                    data.records.get(i).data[data.NumberOfColumns-1].data=(double)1;
                }
            }else {
                for(int j=0;j<data.NumberOfColumns;j++){
                    data.records.get(i).data[data.NumberOfColumns-1].data=(double)-1;
                }
            }
        }

        //starting algorithm
        algorithm();

        // measuring Accuracy on dataset
        accuracyMeasure();
    }
    //*********************************************SUPPORT VECTOR MACHINE**********************************************//
    double gradient[];
    double lambda = 0.05;
    double alpha = 0.5;
    double deltab = 0;

    /**
     * starts the algorithm to find the hyperplane
     */
    void algorithm(){
        System.out.println("Algorithm running...");
        gradient = new double[data.NumberOfColumns-1];
        int iterations = 0;
        while (true){
            iterations++;
            calculateGradient();
            bias -= deltab*alpha;
            for(int i=0;i<data.NumberOfColumns-1;i++){
                weight[i] -= gradient[i]*alpha;
            }
            if(insignificantChange()){
                break;
            }
            if(iterations == MAXIMUMITERATIONS){
                break;
            }
        }
        System.out.println("Model ready...\n" +
                "Number of Iterations done: "+iterations+"\n");
    }

    /**
     * detect if the change is insignificant in the weights and bias
     * @return boolean value
     */
    boolean insignificantChange(){
        for(int i=0;i<data.NumberOfColumns-1;i++){
            if(mod(alpha*gradient[i])>0.0001){
                return false;
            }
        }
        if(mod(alpha*deltab)>0.0001){
            return false;
        }
        return true;
    }

    /**
     * returns the modulus of x
     * @param x
     * @return
     */
    double mod(double x){
        if(x>0){
            return x;
        }else return -x;
    }

    /**
     * calculates the current gradient and stores it in gradient vector(array)
     */
    void calculateGradient(){
        int last = data.NumberOfColumns-1;
        double temp;
        //for each wi
        for(int i=0;i<data.NumberOfColumns-1;i++){
            gradient[i] = 0;
            // for each record
            for(int j=0;j<data.records.size();j++){
                //calculation of max(0,(1-yi(w.xi-b)))
                temp = 0;
                // calculation of yi.w.xi
                for(int k=0;k<data.NumberOfColumns-1;k++){
                    temp += ((double)data.records.get(j).data[k].data)*((double)data.records.get(j).data[last].data)*weight[k];
                }
                // calculation of yi.b
                temp -= bias*((double)data.records.get(j).data[last].data);
                if((1-temp)>0){
                    gradient[i] += -1*((double)data.records.get(j).data[i].data)*((double)data.records.get(j).data[last].data);
                    deltab += (double)data.records.get(j).data[last].data;
                }
            }
            gradient[i] /= (double)data.NumberOfRecords;
            gradient[i] += 2*lambda*weight[i];
        }
        deltab /= (double)data.NumberOfRecords;
    }

    /**
     * prints the weight vector of hyperplane
     */
    public void printWeightVector(){
        for(int i=0;i<data.NumberOfColumns-1;i++){
            System.out.print(weight[i]+" ");
        }
        System.out.println();
        System.out.println(bias);
    }
    //*************************************************PREDICTION*****************************************************//

    /**
     * predicts the class based on currents model
     * @param vector attributes of the data of which the class is to be predicted
     * @return the class of coressponding tuple
     * @since 1.0
     */
    public double predict(double[] vector){
        double x=0;
        for(int i=0;i<data.NumberOfColumns-1;i++){
            x+=weight[i]*vector[i];
        }
        x-=bias;
        if(x>0){
            System.out.println("class: "+classes.get(0));
            return 1;
        }else if(x<0){
            System.out.println("class: "+classes.get(1));
            return -1;
        }else {
            System.out.println("class: "+"uncertain");
            return 0;
        }
    }

    //**************************************ACCURACY ON TRAINING******************************************************//

    /**
     * measures the accuracy on the training data
     * stores accuracy in accuracy variable
     */
    void accuracyMeasure(){
        int acc=0;
        double[] x = new double[data.NumberOfColumns-1];
        for(int i=0;i<data.NumberOfRecords;i++){
            for(int j=0;j<data.NumberOfColumns-1;j++){
                x[j] = (double)data.records.get(i).data[j].data;
            }
            if(predict(x)==(double)data.records.get(i).data[data.NumberOfColumns-1].data){
                acc++;
            }
        }
        Accuracy = acc/(double)data.records.size();
        System.out.println("Accuracy on Training data: "+acc+"/"+data.records.size()+"("+(Accuracy*100)+"%)\n");
    }
    //***************************************TESTING FROM FILE********************************************************//

    /**
     * testing the data from a csv file
     * @param filepath path to file containing test values
     * @since 1.0
     */
    public void testCSVFile(String filepath){
        FileInputStream fin;
        try {
            fin = new FileInputStream(filepath);
        }catch (FileNotFoundException e){
            System.out.println("file not found");
            return;
        }
        int i=0,index=0;
        int state=0;
        String t="";
        double d = 0;
        System.out.println("\nTesting Results...");
        double[] array = new double[data.NumberOfColumns-1];
        try{
            while((i = fin.read())!=-1){
                if(((char)i) == ','){
                    d = Double.parseDouble(t);
                    t = "";
                    array[index++] = d;
                }else if(((char)i) == '\n'){
                    array[index] = d;
                    predict(array);
                    index=0;
                    t="";
                }else{
                    t = t + (char)i;
                }
            }
        }catch (IOException e){
            System.out.println("Error in reading file");
            return;
        }
    }
    //********************************************PLOTTING************************************************************//

    /**
     * plots the data along with hyperplane only if data is two dimensional
     *
     */
    public void plotLine(){
        new ScatterPlot(data,weight[0],weight[1],bias);
    }
}
