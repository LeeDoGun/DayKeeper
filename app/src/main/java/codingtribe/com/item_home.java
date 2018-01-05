package codingtribe.com;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class item_home extends Fragment {

    private int mPosition;

    static item_home newInstance(int position) {
        item_home f = new item_home();	//객체 생성
        Bundle args = new Bundle();					//해당 fragment에서 사용될 정보 담을 번들 객체
        args.putInt("position", position);				//포지션 값을 저장
        f.setArguments(args);							//fragment에 정보 전달.
        return f;											//fragment 반환
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments() != null ? getArguments().getInt("position") : 0;	// 뷰페이저의 position값을  넘겨 받음
    }

    //찬울 : onCreate안에 있던 코드 넣는 곳
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_item_home, container, false);
        //여기!!
        // findViewById 하는법 : v.findViewById
        // Intent 하는법 : new Intent(getgetActivity(), 이동할페이지이름.class);
        return v;
    }
}
