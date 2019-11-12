package com.example.sppb_tfg;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AccData {


    private boolean full_test;
    private ArrayList<String[]> data_matrix = new ArrayList<>();
    //private ArrayList<ArrayList<String>> data_matrix = new ArrayList<ArrayList<String>>();
    //private List<List<String>> data_matrix = new ArrayList<List<String>>();


    private int n_rows = 0;
    private int current_row = 0;

    /*String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    String fileName = "AnalysisSPPB.csv";
    String filePath = baseDir + File.separator + fileName;
    File f = new File(filePath);*/

    File pathfile;
    File filename;
    CSVWriter writer;


    public AccData(Context context, boolean full_test){
        this.full_test = full_test;
        //data_matrix.add(new ArrayList<String[]>());

        /*if (full_test){
            data_matrix.get(0).add("Balance_timestamp");
            data_matrix.get(0).add("B_x");
            data_matrix.get(0).add("B_y");
            data_matrix.get(0).add("B_z");

            data_matrix.get(0).add("Gait_timestamp");
            data_matrix.get(0).add("G_x");
            data_matrix.get(0).add("G_y");
            data_matrix.get(0).add("G_z");

            data_matrix.get(0).add("Chair_timestamp");
            data_matrix.get(0).add("C_x");
            data_matrix.get(0).add("C_y");
            data_matrix.get(0).add("C_z");
        } else {
            data_matrix.get(0).add("timestamp");
            data_matrix.get(0).add("x");
            data_matrix.get(0).add("y");
            data_matrix.get(0).add("z");
        }*/

       if (full_test){
           data_matrix.add(new String[] {"SEP=; " +
                   "\nBalance_timestamp", "B_x", "B_y", "B_z",
                    "Gait_timestamp", "G_x", "G_y", "G_z",
                    "Chair_timestamp", "C_x", "C_y", "C_z"});
        } else {
            data_matrix.add(new String[] {"SEP=; \ntimestamp", "x", "y", "z"});
        }


        pathfile = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "SPPB");
        if (!pathfile.isDirectory()) {
            pathfile.mkdir();
        }

        filename = new File(pathfile,
                File.separator + "csvDataFile.csv");

        if (!filename.exists()) {
            try {
                filename.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void storeData(int test, long t, float x, float y, float z){
        /*if(current_row >= n_rows){
            data_matrix.add(new ArrayList<String>());
            current_row++;
            n_rows++;
        }

        if (!full_test) {
            data_matrix.get(current_row).add(Long.toString(t));
            data_matrix.get(current_row).add(Float.toString(x));
            data_matrix.get(current_row).add(Float.toString(y));
            data_matrix.get(current_row).add(Float.toString(z));
        }*/

        if(current_row >= n_rows){
            data_matrix.add(new String[] {Long.toString(t),
                    String.format ("%.5f", x),
                    String.format ("%.5f", y),
                    String.format ("%.5f", z)});
            current_row++;
            n_rows++;
        }

        /*if (!full_test){
            data_matrix.get(current_row) += {Long.toString(t),
                    String.format ("%.5f", x),
                    String.format ("%.5f", y),
                    String.format ("%.5f", z)};
        }*/

    }

    public void makeCSV() throws IOException {
        Log.d("EXEL", String.valueOf(filename));
        //writer = new CSVWriter(new FileWriter(filename));
        /*writer = new CSVWriter(new FileWriter(filename),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);*/

        FileWriter fw = new FileWriter(filename);
        //writer.writeAll(data_matrix);
        for(int i = 0; i < data_matrix.size(); i++){
            for(int j = 0; j < data_matrix.get(i).length; j++){
                fw.append(data_matrix.get(i)[j]);
                fw.append(';');
            }
            fw.write('\n');
            /*writer.writeNext(data_matrix.get(i));
            //writer.writeNext(data_matrix.get(i));
            Log.d("EXEL", data_matrix.get(i)[i%3]);*/
        }

        fw.close();
    }

}
