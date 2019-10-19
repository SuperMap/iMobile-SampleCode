package com.supermap.imobile.Pop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.supermap.imobile.Dialog.LayerStyleDialog;
import com.supermap.imobile.adapter.MyLayerAdapter;
import com.supermap.imobile.bean.MyLayerData;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.MapControl;


//图层设置pop
public class LayerSettingPop extends PopupWindow {
    Context context;
    View view;
    MapControl mapControl;
    LinearLayout layout_style;
    int position;
    public MyLayerAdapter adapter;

    public LayerSettingPop(MapControl mapControl, int position) {
        this.mapControl = mapControl;
        context = mapControl.getContext();
        this.position = position;
        initView();
    }

    /**
     * 加载布局
     */
    private void initView() {
        view = LayoutInflater.from(context).inflate(R.layout.pop_layersetting, null);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(view);
        setOutsideTouchable(true);

        //设置点击事件
        view.findViewById(R.id.tv_up).setOnClickListener(listener);
        view.findViewById(R.id.tv_down).setOnClickListener(listener);
        view.findViewById(R.id.tv_appoint).setOnClickListener(listener);
        view.findViewById(R.id.tv_setdown).setOnClickListener(listener);
        view.findViewById(R.id.tv_rename).setOnClickListener(listener);
        view.findViewById(R.id.tv_style).setOnClickListener(listener);

        layout_style = (LinearLayout) view.findViewById(R.id.layout_style);
    }

    /**
     * 显示
     * @param view
     */
    public void ShowPop(View view) {
//        showAsDropDown(view);
        View windowContentViewRoot = this.view;
        int windowPos[] = calculatePopWindowPos(view, windowContentViewRoot);
        int xOff = 40;// 可以自己调整偏移
        windowPos[0] -= xOff;
        showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

    /**
     * 若不是点线面图层时，不需要设置其图层风格
     */
    public void hideStylelayout() {
        layout_style.setVisibility(View.GONE);

    }
    MyLayerData tmpdata=null;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_up:
                    if (position - 1 < 0) {
                        shown("已位于顶点");
                        return;
                    }
                    mapControl.getMap().getLayers().moveUp(position);
                    //将上下两个数据进行位置交换
                    tmpdata = adapter.myLayerDataList.get(position);
                    adapter.myLayerDataList.set(position, adapter.myLayerDataList.get(position - 1));
                    adapter.myLayerDataList.set(position - 1, tmpdata);
                    adapter.notifyDataSetChanged();
                    dismiss();
                    mapControl.getMap().refresh();
                    break;
                case R.id.tv_down:
                    if (position + 1 >= adapter.myLayerDataList.size()) {
                        shown("已位于底点");
                        return;
                    }
                    mapControl.getMap().getLayers().moveDown(position);
                    //将上下两个数据进行位置交换
                    tmpdata = adapter.myLayerDataList.get(position);
                    adapter.myLayerDataList.set(position, adapter.myLayerDataList.get(position + 1));
                    adapter.myLayerDataList.set(position + 1, tmpdata);
                    adapter.notifyDataSetChanged();
                    dismiss();
                    mapControl.getMap().refresh();
                    break;
                case R.id.tv_appoint:

                    if (position == 0) {
                        shown("已位于顶点");
                        return;
                    }
                    mapControl.getMap().getLayers().moveToTop(position);

                    tmpdata = adapter.myLayerDataList.get(position);
                    adapter.myLayerDataList.remove(position);//移除当前位置数据
                    adapter.myLayerDataList.add(0, tmpdata);//将其添加在顶部
                    adapter.notifyDataSetChanged();

                    dismiss();
                    mapControl.getMap().refresh();
                    break;
                case R.id.tv_setdown:

                    if (position == adapter.myLayerDataList.size() - 1) {
                        shown("已位于底点");
                        return;
                    }
                    mapControl.getMap().getLayers().moveToBottom(position);
                    tmpdata = adapter.myLayerDataList.get(position);
                    adapter.myLayerDataList.remove(position);//移除当前位置数据
                    adapter.myLayerDataList.add(adapter.myLayerDataList.size(), tmpdata);//将其添加在底部
                    adapter.notifyDataSetChanged();
                    dismiss();
                    mapControl.getMap().refresh();
                    break;
                case R.id.tv_rename:
                    Dialogshow();

                    break;
                case R.id.tv_style:
                    dismiss();
                    LayerStyleDialog dialog = new LayerStyleDialog(mapControl, R.style.MyLayerStyleDialog, adapter.myLayerDataList.get(position));
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                    break;
            }

        }
    };

    private void shown(String s) {
        Toast.makeText(mapControl.getContext(), s, Toast.LENGTH_SHORT).show();
    }

    public void Dialogshow() {
        View view = LayoutInflater.from(context).inflate(R.layout.rename_dialog, null);
        final EditText editText = view.findViewById(R.id.edittext_rename);
        new AlertDialog.Builder(mapControl.getContext())
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editText.getText().toString().equals("")) {
                            shown("名称不能为空");
                            return;
                        }
                        mapControl.getMap().getLayers().get(position).setCaption(editText.getText().toString());
                        adapter.myLayerDataList.get(position).setCaption(editText.getText().toString());
                        adapter.notifyDataSetChanged();
                        dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     *
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    private static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
