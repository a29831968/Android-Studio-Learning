btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                final int y=c.get(Calendar.YEAR);
                final int m=c.get(Calendar.MONTH);
                final int d=c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener listen=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d("answer",year+"/"+(month+1)+"/"+dayOfMonth);
                    }
                };
                DatePickerDialog datePickerDialog=new DatePickerDialog(ProfileActivity.this,listen,y,m,d);
                datePickerDialog.show();
            }
        });
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
final Calendar e=Calendar.getInstance();
eyear=e.get(Calendar.YEAR);
emonth=e.get(Calendar.MONTH);
edate=e.get(Calendar.DAY_OF_MONTH);
DatePickerDialog datePickerDialog = new DatePickerDialog(this,DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT,new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        txv_endDate.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);
        ey=year;
        em=monthOfYear+1;
        ed=dayOfMonth;
        End=txv_endDate.getText().toString().trim();

        mProgress.show();
    }
},eyear,emonth,edate);
datePickerDialog.show();
