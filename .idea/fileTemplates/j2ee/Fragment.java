package ${PACKAGE_NAME};

import com.fengchen.light.ui.BaseFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

#parse("File Header.java")
public class ${NAME} extends BaseFragment<XxxBionding> {
    @Override
    protected void inject() {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_xxx;
    }
}