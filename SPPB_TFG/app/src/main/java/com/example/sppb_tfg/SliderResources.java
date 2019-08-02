package com.example.sppb_tfg;

/*
* When you add an item here, it is automatically updated to be displayed in the corresponding view.
*/
public class SliderResources {
    /*
     * BALANCE TEST
     */
    public int[] balance_images = {
            R.drawable.ic_person_circle,
            R.drawable.ic_feet_1,
            R.drawable.ic_feet_2,
            R.drawable.ic_feet_3,
            R.drawable.ic_desbalan_person_circle
    };

    public int[] balance_headings = {
            R.string.balan_heading1,
            R.string.balan_heading2,
            R.string.balan_heading3,
            R.string.balan_heading4,
            R.string.balan_heading5
    };

    public int[] balance_descs = {
            R.string.balan_descr1,
            R.string.balan_descr2,
            R.string.balan_descr3,
            R.string.balan_descr4,
            R.string.balan_descr5
    };


    /*
     * GAIT SPEED TEST
     */
    public int[] gait_images = {
            R.drawable.ic_sound,
            R.drawable.ic_distance,
            R.drawable.ic_replay
    };

    public int[] gait_headings = {
            R.string.gait_heading1,
            R.string.gait_heading2,
            R.string.gait_heading3
    };

    public int[] gait_descs = {
            R.string.gait_descr1,
            R.string.gait_descr2,
            R.string.gait_descr3,
    };


    /*
     * CHAIR STAND TEST
     */
    public int[] chair_images = {
            R.drawable.ic_sit_person_circle,
            R.drawable.ic_stand_person_circle,
            R.drawable.ic_x5
    };

    public int[] chair_headings = {
            R.string.chair_heading1,
            R.string.chair_heading2,
            R.string.chair_heading3
    };

    public int[] chair_descs = {
            R.string.chair_descr1,
            R.string.chair_descr2,
            R.string.chair_descr3,
    };


    /*
     * COMPLETE ARRAYS
     */
    public int[][] slide_images = {balance_images, gait_images, chair_images};
    public int[][] slide_headings = {balance_headings, gait_headings, chair_headings};
    public int[][] slide_descs = {balance_descs, gait_descs, chair_descs};
}
