package app.android.example.com.poifinder.utilities;

/**
 * Created by ruiguo on 1/11/16.
 */

public class Review {
    private String reviewdBy;
    private String contents;
    private double rating;
    public String getReviewdBy(){return reviewdBy;}
    public void setReviewdBy(String name){reviewdBy=name;}

    public String getContents(){return contents;}
    public void setContents(String con){contents=con;}

    public double getRating(){return rating;}
    public void setRating(double r){rating=r;}

}
