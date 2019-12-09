package com.example.sppb_tfg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DownloadHistData {

    Context mContext;
    User mUser;
    File pathfile;
    File filename;
    private ArrayList<ArrayList<String>> data_matrix = new ArrayList<>();
    private int n_columns = 0;


    public DownloadHistData(Context context, User user) {
        mContext = context;
        mUser = user;

        // Add the headers for each column
        String[] headers = {"Date", "Total_score", "Balance_score",
                "Gait_score", "Chair_score", "Average_speed_(m/s)",
                "Age", "Weight", "Height", "BMI"};
        n_columns = headers.length;

        for (int i = 0; i < n_columns; i++) {
            data_matrix.add(new ArrayList<String>());
            data_matrix.get(i).add(headers[i]);
        }

        saveData();
    }

    public void saveData() {
        int historySize = mUser.getHistorySize();

        for (int i = 0; i < historySize; i++) {
            data_matrix.get(0).add(mUser.getTestDate(i));
            data_matrix.get(1).add(String.valueOf(mUser.getScore(i)));
            data_matrix.get(2).add(String.valueOf(mUser.getBalanceScore(i)));
            data_matrix.get(3).add(String.valueOf(mUser.getSpeedScore(i)));
            data_matrix.get(4).add(String.valueOf(mUser.getChairScore(i)));
            data_matrix.get(5).add(String.format("%.2f", mUser.getAverageSpeed(i)));
        }

        data_matrix.get(6).add(String.valueOf(mUser.getAge()));
        data_matrix.get(7).add(String.valueOf(mUser.getWeight()));
        data_matrix.get(8).add(String.valueOf(mUser.getHeight()));
        data_matrix.get(9).add(String.format("%.2f", mUser.getBMI()));
    }

    // This function return the total number of rows in the longest column
    public int getLongestColumn() {
        return data_matrix.get(0).size();
    }

    public void makeCSV() throws IOException {
        // NOT DEFINITIVE - Creating file
        pathfile = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "SPPB");
        if (!pathfile.isDirectory()) {
            pathfile.mkdir();
        }

        filename = new File(pathfile,
                File.separator + mUser.getName() + ".csv");

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
        for (int i = 0; i < longestColumn; i++) {
            for (int j = 0; j < n_columns; j++) {
                if (data_matrix.get(j).size() > i) {
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
