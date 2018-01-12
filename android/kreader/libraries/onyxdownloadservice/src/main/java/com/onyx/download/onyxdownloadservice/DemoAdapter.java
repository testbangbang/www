package com.onyx.download.onyxdownloadservice;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 12 on 2017/1/18.
 */

public class DemoAdapter extends RecyclerView.Adapter {
    private List<DownloadRequest> datas = new ArrayList<DownloadRequest>();
    private List<DemoViewHolder> viewHolderList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_demo, null);
        DemoViewHolder viewHolder = new DemoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DemoViewHolder viewHolder = (DemoViewHolder) holder;
        viewHolder.update(datas.get(position).getTaskId(), position);
        viewHolder.taskActionBtn.setTag(viewHolder);
        viewHolder.taskActionBtn.setOnClickListener(taskActionOnClickListener);
        viewHolder.taskNameTv.setText(datas.get(position).getTag());
        viewHolderList.add(viewHolder);
        //viewHolder.taskActionBtn.setEnabled(true);

        if (DownloadTaskManager.getInstance().isReady()) {
        final int status = DownloadTaskManager.getInstance().getStatus(datas.get(position));
        if (DownloadTaskManager.getInstance().isPending(status) || DownloadTaskManager.getInstance().isStarted(status) ||
                DownloadTaskManager.getInstance().isConnected(status)) {
            // start task, but file not created yet
            viewHolder.updateDownloading(status, DownloadTaskManager.getInstance().getSoFar(datas.get(position).getTaskId())
                    , DownloadTaskManager.getInstance().getTotal(datas.get(position).getTaskId()));
        } else if (!new File(datas.get(position).getPath()).exists() &&
                !DownloadTaskManager.getInstance().getTempPath(datas.get(position)).exists()) {
            // not exist file
            viewHolder.updateNotDownloaded(status, 0, 0);
        } else if (DownloadTaskManager.getInstance().isDownloaded(status)) {
            // already downloaded and exist
            viewHolder.updateDownloaded();
        } else if (DownloadTaskManager.getInstance().isDownloading(status)) {
            // downloading
            viewHolder.updateDownloading(status, DownloadTaskManager.getInstance().getSoFar(datas.get(position).getTaskId())
                    , DownloadTaskManager.getInstance().getTotal(datas.get(position).getTaskId()));
        } else {
            // not start
            viewHolder.updateNotDownloaded(status, DownloadTaskManager.getInstance().getSoFar(datas.get(position).getTaskId())
                    , DownloadTaskManager.getInstance().getTotal(datas.get(position).getTaskId()));
        }
        } else {
            viewHolder.taskStatusTv.setText(R.string.tasks_manager_demo_status_loading);
            //viewHolder.taskActionBtn.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setData(List<DownloadRequest> lists) {
        datas.addAll(lists);
        notifyDataSetChanged();
    }

    private View.OnClickListener taskActionOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getTag() == null) {
                return;
            }
            final DemoViewHolder holder = (DemoViewHolder) view.getTag();

            CharSequence action = ((TextView) view).getText();
            if (action.equals(view.getResources().getString(R.string.pause))) {
                DownloadTaskManager.getInstance().pause(holder.id);
            } else if (action.equals(view.getResources().getString(R.string.start))) {
                DownloadTaskManager.getInstance().addDownloadCallback(datas.get(holder.position),
                        new DownloadCallback() {
                            @Override
                            public void progressChanged(int reference, String title, String remoteUri, String localUri,
                                                        int state, long finished, long total, long percentage) {

                                EventBus.getDefault().post(new InfoEvent(reference,state,finished,total));
                            }
                        });
            } else if (action.equals(view.getResources().getString(R.string.delete))) {
                DownloadTaskManager.getInstance().delete(datas.get(holder.position));
                holder.updateNotDownloaded(datas.get(holder.position).getState(), 0, 0);
            }
        }
    };

    public void setInfo(InfoEvent event) {
        for (DemoViewHolder holder : viewHolderList) {
            if(holder.id == event.reference){
                if (DownloadTaskManager.getInstance().isError(event.state) ||
                        DownloadTaskManager.getInstance().isPause(event.state)) {
                    holder.updateNotDownloaded(event.state, event.finished, event.total);
                } else if (DownloadTaskManager.getInstance().isDownloaded(event.state)) {
                    holder.updateDownloaded();
                } else {
                    holder.updateDownloading(event.state, event.finished, event.total);
                }
            }
        }
    }

    private static class DemoViewHolder extends RecyclerView.ViewHolder {

        private TextView taskNameTv;
        private TextView taskStatusTv;
        private ProgressBar taskPb;
        private Button taskActionBtn;
        private int id;
        private int position;

        public DemoViewHolder(View itemView) {
            super(itemView);
            assignViews(itemView);
        }

        private void assignViews(View itemView) {
            taskNameTv = (TextView) itemView.findViewById(R.id.task_name_tv);
            taskStatusTv = (TextView) itemView.findViewById(R.id.task_status_tv);
            taskPb = (ProgressBar) itemView.findViewById(R.id.task_pb);
            taskActionBtn = (Button) itemView.findViewById(R.id.task_action_btn);
        }

        public void update(final int id, final int position) {
            this.id = id;
            this.position = position;
        }

        public void updateDownloaded() {
            taskPb.setMax(1);
            taskPb.setProgress(1);

            taskStatusTv.setText(R.string.tasks_manager_demo_status_completed);
            taskActionBtn.setText(R.string.delete);
        }

        public void updateNotDownloaded(final int status, final long sofar, final long total) {
            if (sofar > 0 && total > 0) {
                final float percent = sofar
                        / (float) total;
                taskPb.setMax(100);
                taskPb.setProgress((int) (percent * 100));
            } else {
                taskPb.setMax(1);
                taskPb.setProgress(0);
            }

            if (DownloadTaskManager.getInstance().isError(status)) {
                taskStatusTv.setText(R.string.tasks_manager_demo_status_error);
            } else if (DownloadTaskManager.getInstance().isPause(status)) {
                taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
            } else {
                taskStatusTv.setText(R.string.tasks_manager_demo_status_not_downloaded);
            }

            taskActionBtn.setText(R.string.start);
        }

        public void updateDownloading(final int status, final long sofar, final long total) {
            final float percent = sofar
                    / (float) total;
            taskPb.setMax(100);
            taskPb.setProgress((int) (percent * 100));

            if (DownloadTaskManager.getInstance().isPending(status)) {
                taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
            } else if (DownloadTaskManager.getInstance().isStarted(status)) {
                taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
            } else if (DownloadTaskManager.getInstance().isConnected(status)) {
                taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
            } else if (DownloadTaskManager.getInstance().isDownloading(status)) {
                taskStatusTv.setText(R.string.tasks_manager_demo_status_progress);
            } else {
                taskStatusTv.setText(DownloadTaskManager.getContext().getString(
                        R.string.tasks_manager_demo_status_downloading, status));
            }

            taskActionBtn.setText(R.string.pause);
        }
    }
}
