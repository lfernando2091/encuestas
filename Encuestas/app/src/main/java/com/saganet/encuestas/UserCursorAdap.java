package com.saganet.encuestas;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 * Created by Luis Fernando on 21/10/2016.
 */

public class UserCursorAdap extends CursorAdapter {

    public UserCursorAdap(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.activity_item_user, parent, false);
    }

    private void userData(Cursor cursor, TextView nameText, TextView SetStatus){
        // Get valores.
        String name = cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_NOMBRE));
        name = name + " " + cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PATERNO));
        name = name + " " + cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_MATERNO));
        Log.v("El nombre del usuario:", name);
        //tv_status_user
        // Setup.
        nameText.setText(name);
        SetStatus.setText("");
        SetStatus.setVisibility(View.GONE);
        try {
            String val=cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PKEY));
            int v= Integer.parseInt(val);
        }catch (Exception e){
            SetStatus.setVisibility(View.VISIBLE);
            SetStatus.setText("Agregado manualmente");
        }
    }
    private void EncuestaData(Cursor cursor, TextView nameText, TextView SetStatus){
        // Get valores.
        String name = cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ENCUESTA));
        Log.v("El nombre encuesta:", name);
        // Setup.
        SetStatus.setText("");
        SetStatus.setVisibility(View.GONE);
        nameText.setText(name);
    }

    @Override
    public void bindView(View view,final Context context, Cursor cursor) {
        // Referencias UI.
        TextView nameText = (TextView) view.findViewById(R.id.tv_name);
        TextView nameTextStatus = (TextView) view.findViewById(R.id.tv_status_user);
        final ImageView avatarImage = (ImageView) view.findViewById(R.id.iv_avatar);
        //userData(cursor, nameText);
        try{
            switch (DataUpload.USER_TYPE_RESULT){
                case 1:
                    EncuestaData(cursor, nameText,nameTextStatus);
                    break;
                case 2:
                    userData(cursor, nameText,nameTextStatus);
                    break;
            }
        }catch (Exception e){
            Log.v("ERROR EN ",e.getMessage());
        }
        //String avatarUri = cursor.getString(cursor.getColumnIndex("test.png"));
        String avatarUri = "test.png";
        Glide
                .with(context)
                .load(Uri.parse("file:///android_asset/" + avatarUri))
                .asBitmap()
                .error(R.drawable.ic_account_circle)
                .centerCrop()
                .into(new BitmapImageViewTarget(avatarImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable
                                = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        drawable.setCircular(true);
                        avatarImage.setImageDrawable(drawable);
                    }
                });
    }
}
