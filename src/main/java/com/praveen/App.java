package com.praveen;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws Exception {
        Prediction solution = new Prediction();


        //Grand Canyon
        String prediction = solution.flyby(36.098592,-112.097796);

        System.out.println("Next Predicted Fly by Date and Time for Grand Canyon is  :"+prediction);

        //San Francisco
        prediction = solution.flyby(37.7937007 ,-122.4039064);

        System.out.println("Next Predicted Fly by Date and Time for Delphix San Francisco is  :"+prediction);

        //Niagra Falls
        prediction = solution.flyby(43.078154,-79.075891);

        System.out.println("Next Predicted Fly by Date and Time for Niagra Falls is  :"+prediction);

        //Four Corners Monument
        prediction = solution.flyby(36.998979,-109.045183);

        System.out.println("Next Predicted Fly by Date and Time for Four Corners Monument  is  :"+prediction);

        //Edge Cases will throw Exception when
        prediction = solution.flyby(0,0);

        System.out.println("Next Predicted Fly by Date and Time for Zero  is  :"+prediction);

    }
}
