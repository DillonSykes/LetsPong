
package dsykes.letspong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * C
 */

public class CustomAdapter extends ArrayAdapter<Match>  {
    private Viewholder myVh;
    ArrayList<Match> results;
    ArrayList<Users> users;
    private LayoutInflater inflater;
    private final Context ctx;
    private String MYURL;

    public CustomAdapter(Context context, int resource, ArrayList<Match> results, ArrayList<Users> users) {
        super(context, resource, results);
        this.ctx = context;
        this.results = results;
        this.users = users;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Match getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class Viewholder {
        TextView score;
        TextView tv_description;
        TextView opponent;
        ImageView tv_image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Viewholder myVH;

        //if cannot recycle, then create a new one, this should only happen
        //with first screen of data (or rows)
        if (convertView == null) {
            if (inflater == null)
                inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //get a row
            convertView = inflater.inflate(R.layout.listview_row_layout, null);

            //create a viewholder for effeciency (and for thread usage)
            myVH = new Viewholder();

            //get refs to widgets
            myVH.score = (TextView) convertView.findViewById(R.id.Score);
            myVH.tv_description = (TextView) convertView.findViewById(R.id.Description);
            myVH.opponent = (TextView) convertView.findViewById(R.id.Opp);
            myVH.tv_image = (ImageView) convertView.findViewById(R.id.imageView1);

            //marry the viewholder to the convertview row
            convertView.setTag(myVH);
        }


        myVH = (Viewholder) convertView.getTag();

        //set the first field
        String score = Match.formatScore(getItem(position));
        myVH.score.setText(score);
        myVH.tv_description.setText(getItem(position).getMatchType());
        String OppUID = Match.myOpp(getItem(position));
        String OppName = Users.findUserName(OppUID,users);
        if(getItem(position).getWinner().equals(OppName)){
            myVH.tv_image.setImageResource(R.mipmap.loser);
        } else {
            myVH.tv_image.setImageResource(R.mipmap.winner);

        }
        myVH.opponent.setText(OppName);
        return convertView;
    }

}
