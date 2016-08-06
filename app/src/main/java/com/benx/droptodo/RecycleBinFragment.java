package com.benx.droptodo;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/6
 */

public class RecycleBinFragment extends Fragment {
    /**
     * *********** 常量 ***********
     */
    private final List<ToDo> DeleteToDoList;    // 删除的待办事项

    public ToDoAdapter toDoAdapter;             // 适配器
    private ItemTouchHelper itemTouchHelper;    // ItemTouchHelper 实例

    public RecyclerView recyclerView;           // RecyclerView 实例
    public static float Elevation;              // itemView 的抬起度


    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\nHH:mm  ");
    // 时间格式


    //// TODO: 2016/8/6 linshi 
    public String TAG = "lll";



    /**
     * *********** 构造方法 ***********
     */

    public RecycleBinFragment() {
        Log.d(TAG, "RecycleBinFragment: new");
        toDoAdapter = new ToDoAdapter();

        // 获取 MainActivity 中的数据集
        // TODO: 2016/8/6 如果要还原则直接操作并还原 
        //ToDoList = MainActivity.ToDoList;
        DeleteToDoList = MainActivity.DeleteToDoList;
        Log.d(TAG, "RecycleBinFragment: get deletelist size:"+DeleteToDoList.size());
    }





    /**
     * *********** 重写方法 -- Fragment ***********
     */
    /**
     * 当 Fragment 新建 View 时
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return new RecyclerView(container.getContext());
    }



    /**
     * 当 View 被创建后
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);

        // 参数view是我们再onCreateView中返回的view
        recyclerView = (RecyclerView) view;

        // 固定recyclerview的大小
        recyclerView.setHasFixedSize(true);

        // 设置adapter
        recyclerView.setAdapter(toDoAdapter = new ToDoAdapter());

        // 设置布局类型 -- LinearLayoutManager 相当于ListView的样式
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 设置监听 实例itemTouchHelper
        setListener();
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    /**
     * *********** 自定义方法  ***********
     */

    /**
     *  设置监听
     *
     */
    private void setListener() {
        // 新建一个 ItemTouchHelper 实例    （onMove onSwiped 等）
        Log.d(TAG, "setListener: get");
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            /**
             * 控制是否可以侧滑
             *
             * @return 返回主活动的控制标志
             */
            @Override
            public boolean isItemViewSwipeEnabled() {
                return MainActivity.swappable;
            }


            /**
             * 控制是否可以长按拖动
             *
             * @return 返回住活动的控制标志
             */
            @Override
            public boolean isLongPressDragEnabled() {
                return MainActivity.draggable;
            }



            /**
             * 设置限定拖动和侧滑的方向
             *
             * @param recyclerView
             * @param viewHolder
             * @return
             */
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                int dragFlags = 0, swipeFlags = 0;

                // TODO 考虑删除瀑布流部分，在别的fragment再使用
                // 如果 RecyclerView 是瀑布流布局
                if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {

                    // 规定拖拽方向为四个方向任意
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

                } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                    // 规定拖拽方向为上下
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

                    //  规定侧滑方向为左右
                    swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                }

                // 提交设置
                return makeMovementFlags(dragFlags, swipeFlags);
            }



            /**
             * 拖动 item 时回调
             *
             * @param recyclerView
             * @param viewHolder
             * @param target
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                // 如果两个 item 不是一个类型，不可以拖拽
                if (viewHolder.getItemViewType() != target.getItemViewType()) {
                    return false;
                }

                // 获取原位置和目标位置
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                // 交换数据集
                Collections.swap(DeleteToDoList, from, to);

                // 交换 item
                toDoAdapter.notifyItemMoved(from, to);
                return true;
            }



            /**
             * 侧滑 item 时回调
             *
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                // 删除 item
                // 具体操作细节在 ToDoAdapter.removeItem() 中
                toDoAdapter.removeItem(viewHolder.getAdapterPosition());

            }


            /**
             * item 状态改变时回调
             *
             * @param viewHolder
             * @param actionState
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);

                // item 被拖拽时
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {

                    // 设置拖拽时 item 浮起的变化
                    viewHolder.itemView.setTranslationZ(100);
                    viewHolder.itemView.setScaleX((float) 0.95);
                    viewHolder.itemView.setScaleY((float) 0.95);
                }

            }



            /**
             * 对 item 拖拽或侧滑完成（或撤销）时回调，清除临时被改变的状态
             *
             * @param recyclerView
             * @param viewHolder
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                // 设置透明度返回原状
                viewHolder.itemView.setAlpha(1.0f);

                // 设置返回时 item 落下的变化
                viewHolder.itemView.setTranslationZ(0);
                viewHolder.itemView.setScaleX((float) 1);
                viewHolder.itemView.setScaleY((float) 1);
            }



            /**
             * item 侧滑时被调用
             *
             * @param c
             * @param recyclerView
             * @param viewHolder
             * @param dX
             * @param dY
             * @param actionState
             * @param isCurrentlyActive
             */
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                // 随侧滑位移改变 item 透明度
                viewHolder.itemView.setAlpha(1 - Math.abs(dX) / MainActivity.ScreenWidth);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
    }

    /**
     * *********** 内部类  ***********
     */
    /**
     * ------ 定义 ToDoAdapter 类
     */
    class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> implements View.OnClickListener {

        /**
         * 创建 ViewHolder 时调用
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_todolist, parent, false);

            ToDoViewHolder holder = new ToDoViewHolder(view);

            view.setOnClickListener(this);
            return holder;
        }


        /**
         * 为 item 绑定数据
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(final ToDoViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: called");
            // 获取重要参数
            final int _position = position;     // 当前位置


            // 获取数据集中的目标数据
            final ToDo _todo = DeleteToDoList.get(position);

            // 设置显示数据

            // 优先级
            holder.todoPriorityBar.setBackground(getPriorityBar(position));

            // 标题
            holder.todoTitleText.setText(_todo.todoTitle);

            // 待办时间
            holder.todoTimeText.setText(dateFormat.format(new Date(_todo.todoTime)));

            // 提醒
            holder.todoItem1Text.setText(_todo.todoItem1);
            holder.todoItem2Text.setText(_todo.todoItem2);
            holder.todoItem3Text.setText(_todo.todoItem3);

            // CheckBox
            holder.todoDoneBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // check
                    if (isChecked) {
                        // 设置对应数据的完成状态
                        _todo.todoDone = true;

                        // 设置对应 item 的显示状态
                        // 落下
                        holder.itemView.setElevation(0);

                    } else {
                        // 设置对应数据的完成状态
                        _todo.todoDone = false;

                        // 设置对应 item 的显示状态
                        // 浮起
                        holder.itemView.setElevation(Elevation);

                        // TODO: 2016/8/3 还需要动态添加背景颜色恢复
                    }
                }
            });
            holder.todoDoneBox.setChecked(_todo.todoDone);  // 根据数据完成状态显示

            // 拖动钮
            // TODO: 2016/8/3 实现方法大致入checkbox 根据数据reorder模式显示或隐藏（设计CheckBox）
//            if (_todo.todoReorderMode == ToDo.REORDER_NORMALMODE) {
//                holder.todoDoneBox.setVisibility(View.VISIBLE);
//                holder.todoReorderButton.setVisibility(View.GONE);
//            } else if (_todo.todoReorderMode == ToDo.REORDER_QUICKMODE) {
//                holder.todoDoneBox.setVisibility(View.GONE);
//                holder.todoReorderButton.setVisibility(View.VISIBLE);
//            }



            // 跑马灯显示
            if (!MainActivity.isMarquee) {
                holder.todoTitleText.setMarqueeRepeatLimit(0);
                holder.todoTitleText.setEllipsize(TextUtils.TruncateAt.END);
                holder.todoItem1Text.setMarqueeRepeatLimit(0);
                holder.todoItem1Text.setEllipsize(TextUtils.TruncateAt.END);
                holder.todoItem2Text.setMarqueeRepeatLimit(0);
                holder.todoItem2Text.setEllipsize(TextUtils.TruncateAt.END);
                holder.todoItem3Text.setMarqueeRepeatLimit(0);
                holder.todoItem3Text.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                holder.todoTitleText.setMarqueeRepeatLimit(-1);
                holder.todoTitleText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                holder.todoItem1Text.setMarqueeRepeatLimit(-1);
                holder.todoItem1Text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                holder.todoItem2Text.setMarqueeRepeatLimit(-1);
                holder.todoItem2Text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                holder.todoItem3Text.setMarqueeRepeatLimit(-1);
                holder.todoItem3Text.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            }

        }



        /**
         * 获取数据集大小
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return DeleteToDoList.size();
        }


        /**
         * item 点击时回调
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: call");

            if (!MainActivity.clickable) {
                return;
            }

            // 获取点击的位置
            final int position = recyclerView.getChildAdapterPosition(v);

            // TODO: 2016/8/6 用snack替换，实现回收
            SnackbarHelper.LongSnackbar(getView(),"Sure to recycle "+DeleteToDoList.get(position).todoTitle+" ?", SnackbarHelper.Info).setAction("Yes", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.ToDoList.add(DeleteToDoList.get(position));
                    DeleteToDoList.remove(position);
                    notifyDataSetChanged();
                    SnackbarHelper.LongSnackbar(getView(), "Check it in your Todo list.", SnackbarHelper.Confirm).setActionTextColor(Color.DKGRAY).show();
                }
            }).setActionTextColor(Color.DKGRAY).show();
        }



        /**
         * *********** 自定义方法  ***********
         */
        /**
         *
         *
         * @param position
         * @return
         */
        public Drawable getPriorityBar(int position) {

            // 获取数据集中对应的优先级
            int priority = DeleteToDoList.get(position).todoPriority;

            // 声明一个 Drawable 资源
            Drawable drawableResource = null;

            switch (priority){
                case 10:
                    drawableResource = getResources().getDrawable(R.drawable.priority_v_important,null);
                    break;
                case 11:
                    drawableResource = getResources().getDrawable(R.drawable.priority_important,null);
                    break;
                case 12:
                    drawableResource = getResources().getDrawable(R.drawable.priority_normal,null);
                    break;
                case 13:
                    drawableResource = getResources().getDrawable(R.drawable.priority_casual,null);
                    break;
            }

            return drawableResource;
        }



        /**
         * ------ 定义 ToDoViewHolder 类
         */
        class ToDoViewHolder extends RecyclerView.ViewHolder {
            /**
             * 变量
             */
            private ImageView todoPriorityBar;          // 优先级条
            private TextView todoTitleText;             // 主题栏
            private TextView todoTimeText;              // 时间栏
            private TextView todoItem1Text;             // 提醒栏1
            private TextView todoItem2Text;             // 提醒栏2
            private TextView todoItem3Text;             // 提醒栏3
            private CheckBox todoDoneBox;               // CheckBox
            private ImageView todoReorderButton;        // 拖动钮



            /**
             * 构造方法
             *
             * @param itemView
             */
            public ToDoViewHolder(View itemView) {
                super(itemView);
                // 获取默认的抬升值
                Elevation = itemView.getElevation();

                // 获取对应的控件
                todoPriorityBar = (ImageView) itemView.findViewById(R.id.todo_priority);
                todoTitleText = (TextView) itemView.findViewById(R.id.todo_title);
                todoTimeText = (TextView) itemView.findViewById(R.id.todo_time);
                todoItem1Text = (TextView) itemView.findViewById(R.id.todo_list_1);
                todoItem2Text = (TextView) itemView.findViewById(R.id.todo_list_2);
                todoItem3Text = (TextView) itemView.findViewById(R.id.todo_list_3);
                todoDoneBox = (CheckBox) itemView.findViewById(R.id.todo_done);
                todoReorderButton = (ImageView) itemView.findViewById(R.id.todo_reorder);
            }
        }



        /**
         * *********** 自定义方法  ***********
         */
        /**
         * 添加新的 item
         *
         * @param todo
         * @param position
         */
        public void addItem(ToDo todo, int position) {

            // 添加到数据集
            DeleteToDoList.add(position, todo);

            // 添加 item
            notifyItemInserted(position);

            // 定位到新添加的item的位置
            // TODO: 2016/8/3 需要确认是否有必要保留
            recyclerView.scrollToPosition(position);
        }

        /**
         * 删除旧的 item
         *
         * @param position
         */
        public void removeItem(final int position) {
            Log.d("getin", "removeItem: called");

            // 从数据集中缓存要删除的待办事项
            final ToDo todo = DeleteToDoList.get(position);
            Log.d("getin", "removeItem: want to remove: "+todo.todoTitle);

            // 从数据集中删除
            DeleteToDoList.remove(position);

            // 删除 item
            notifyItemRemoved(position);

            // 询问是否撤销
            // TODO: 2016/8/6
            SnackbarHelper.LongSnackbar(getView(),"Sure to delete "+todo.todoTitle+" forever?", SnackbarHelper.Warning).setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItem(todo,position);
                    SnackbarHelper.LongSnackbar(getView(), "It's back now.", SnackbarHelper.Confirm).setActionTextColor(Color.DKGRAY).show();
                }
            }).setActionTextColor(Color.WHITE).show();



            Log.d("getin", "removeItem: forever delete a todo : "+todo.todoTitle);
            // TODO: 2016/8/6 删除集添加成功


            // TODO: 2016/8/3 需要添加撤销功能
        }


    }

}
