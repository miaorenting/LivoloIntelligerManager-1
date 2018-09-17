package livolo.com.livolointelligermanager.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import livolo.com.livolointelligermanager.R;

/**
 * Created by mayn on 2018/7/6.
 */

public class EditLimitUtil {

    public static void setEditLimit(EditText edit, final int size, final Context context){
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                Log.e("--------------","----start:"+start+"------count:"+count+"----after:"+after);
                String textStr = charSequence.toString();
                if (textStr.length()>size && after>0){
                    DialogUtil.createEmptyMsgDialog(context, R.string.beyond_str_length);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static boolean isLimit(EditText editText,int size,Context context){
        boolean b = editText.getText().toString().length()<=size;
        if (!b){
            DialogUtil.createEmptyMsgDialog(context, R.string.beyond_str_length);
        }
        return b;
    }

}
