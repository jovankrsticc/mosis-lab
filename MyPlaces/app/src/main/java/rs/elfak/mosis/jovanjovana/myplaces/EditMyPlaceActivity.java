package rs.elfak.mosis.jovanjovana.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditMyPlaceActivity extends AppCompatActivity implements View.OnClickListener {

    int position=-1;
    boolean editMode=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_place);
        try
        {
            Intent listIntent=getIntent();
            Bundle positionBundle=listIntent.getExtras();
            if(positionBundle!=null)
                position=positionBundle.getInt("position");
            else
                editMode=false;
        }
        catch(Exception e)
        {
            editMode=false;
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Button finishedButton=(Button)findViewById(R.id.editmyplace_finished_button);

        //finishedButton.setText("Add");
        Button cancelButton=(Button)findViewById(R.id.editmyplace_cancel_button);

        EditText nameEditText=(EditText)findViewById(R.id.editmyplace_name_edit);
       if(!editMode)
       {
           finishedButton.setEnabled(false);
           finishedButton.setText("Add");
       }
       else if(position>=0)
       {
           finishedButton.setText("Save");
           MyPlace place=MyPlacesData.getInstance().getPlace(position);
           nameEditText.setText(place.getName());
           EditText descEditText=(EditText)findViewById(R.id.editmyplace_desc_edit);
           descEditText.setText(place.getDescription());
       }
        finishedButton.setOnClickListener(this);
        finishedButton.setEnabled(false);
        cancelButton.setOnClickListener(this);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                finishedButton.setEnabled(editable.length()>0);
            }
        });


    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.editmyplace_finished_button:
            {
                EditText etName=(EditText) findViewById(R.id.editmyplace_name_edit);
                String nme=etName.getText().toString();
                EditText etDesc=(EditText) findViewById(R.id.editmyplace_desc_edit);
                String desc=etDesc.getText().toString();
                if(!editMode)
                {
                    MyPlace place= new MyPlace(nme,desc);
                    MyPlacesData.getInstance().addNewPlace(place);
                }
               else
                {
                    MyPlace place=MyPlacesData.getInstance().getPlace(position);
                    place.setName(nme);
                    place.setDesc(desc);
                }
                setResult(Activity.RESULT_OK);
                finish();
                break;
            }
            case R.id.editmyplace_cancel_button:
            {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_edit_my_place,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.show_map_item)
        {
            Toast.makeText(this,"Show Map!", Toast.LENGTH_SHORT).show();

        }
        else if(id==R.id.my_places_list_item)
        {
            Intent i=new Intent(this,MyPlacesList.class);
            startActivity(i);
        }
        else if(id==R.id.about_item)
        {
            Intent i=new Intent(this,About.class);
            startActivity(i);
        }
        else if(id==android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
