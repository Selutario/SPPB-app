package com.example.sppb_tfg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DownloadAccData {

    Context mContext;
    private ArrayList<ArrayList<String>> data_matrix = new ArrayList<>();

    private boolean full_test;
    private int n_columns = 0;

    File pathfile;
    File filename;
    CSVWriter writer;


    public DownloadAccData(Context context, boolean full_test){
        this.full_test = full_test;
        mContext = context;

        // Add the headers for each column depending on the number of tests that will be performed.
        if (full_test){
            String[] headers = {"Balance_timestamp", "B_x", "B_y", "B_z",
                    "Gait_timestamp", "G_x", "G_y", "G_z",
                    "Chair_timestamp", "C_x", "C_y", "C_z"};
            n_columns = headers.length;

            for(int i = 0; i < n_columns; i++){
                data_matrix.add(new ArrayList<String>());
                data_matrix.get(i).add(headers[i]);
            }
        } else {
            String[] headers = {"timestamp", "x", "y", "z"};
            n_columns = headers.length;

            for(int i = 0; i < n_columns; i++) {
                data_matrix.add(new ArrayList<String>());
                data_matrix.get(i).add(headers[i]);
            }
        }
    }

    public void storeData(int test, long t, float x, float y, float z){
        // Store the data of each test in its corresponding column (according to the number of test)
        if (!full_test || test == 1){
            data_matrix.get(0).add(Long.toString(t));
            data_matrix.get(1).add(String.format ("%.5f", x));
            data_matrix.get(2).add(String.format ("%.5f", y));
            data_matrix.get(3).add(String.format ("%.5f", z));
        } else if (test == 2){
            data_matrix.get(4).add(Long.toString(t));
            data_matrix.get(5).add(String.format ("%.5f", x));
            data_matrix.get(6).add(String.format ("%.5f", y));
            data_matrix.get(7).add(String.format ("%.5f", z));
        } else if (test == 3){
            data_matrix.get(8).add(Long.toString(t));
            data_matrix.get(9).add(String.format ("%.5f", x));
            data_matrix.get(10).add(String.format ("%.5f", y));
            data_matrix.get(11).add(String.format ("%.5f", z));
        }
    }

    // This function return the total number of rows in the longest column
    public int getLongestColumn() {
        int longest = data_matrix.get(0).size();

        if (full_test){
            int gait = data_matrix.get(4).size();
            int chair = data_matrix.get(8).size();

            if (longest > gait && longest> chair){
            } else if (gait > chair) {
                longest = gait;
            } else {
                longest = chair;
            }
        }
    return longest;
    }

    public void makeCSV(String name) throws IOException {
        // NOT DEFINITIVE - Creating file
        pathfile = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "SPPB");
        if (!pathfile.isDirectory()) {
            pathfile.mkdir();
        }

        /*filename = new File(pathfile,
                File.separator + "csvDataFile.csv");*/
        filename = new File(pathfile,
                File.separator + name + ".csv");

        if (!filename.exists()) {
            try {
                filename.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Declaring file output writer
        FileWriter fw = new FileWriter(filename);
        fw.append("SEP=;\n");   // Set ; as delimiter in csv file

        int longestColumn = getLongestColumn();

        // Fill the file from left to right and from top to bottom.
        // If there is a column with no rows next to a column that is currently writing rows, it is
        // filled with as many ; delimiters as necessary.
        for(int i = 0; i < longestColumn; i++){
            for(int j = 0; j < n_columns; j++){
                if (data_matrix.get(j).size() > i){
                    fw.append(data_matrix.get(j).get(i));
                    fw.append(";");
                } else {
                    fw.append(";");
                }
            }
            fw.append('\n');
        }
        fw.close();

        // Open sharemenu to send the csv file
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
        sharingIntent.setType("text/csv");
        mContext.startActivity(Intent.createChooser(sharingIntent, "share file with"));
    }

}
