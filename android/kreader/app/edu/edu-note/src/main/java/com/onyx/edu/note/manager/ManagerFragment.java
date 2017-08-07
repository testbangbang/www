package com.onyx.edu.note.manager;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.edu.note.databinding.FragmentManagerBinding;
import com.onyx.edu.note.databinding.NoteItemBinding;
import com.onyx.edu.note.ui.BindingViewHolder;
import com.onyx.edu.note.ui.PageAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/6/8 15:12.
 */
public class ManagerFragment extends Fragment {
    static final String TAG = ManagerFragment.class.getSimpleName();

    FragmentManagerBinding mBinding;
    PageAdapter<NoteItemViewHolder, NoteModel, ManagerItemViewModel> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentManagerBinding.inflate(inflater, container, false);
        mBinding.setView(this);
        mBinding.setViewModel(mManagerViewModel);
        initRecyclerView();
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mManagerViewModel.start();
    }

    public void setViewModel(ManagerViewModel mManagerViewModel) {
        this.mManagerViewModel = mManagerViewModel;
    }

    ManagerViewModel mManagerViewModel;

    public ManagerFragment() {
        // Required empty public constructor
    }

    public static ManagerFragment newInstance() {
        return new ManagerFragment();
    }


    private void initRecyclerView() {
        PageRecyclerView resultRecyclerView = mBinding.notePageRecyclerView;
        resultRecyclerView.setHasFixedSize(true);
        resultRecyclerView.setLayoutManager(new DisableScrollGridManager(getContext()));
        buildAdapter();
        resultRecyclerView.setAdapter(mAdapter);
        resultRecyclerView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageStatus();
            }
        });
    }

    private void updatePageStatus() {
        if (mBinding.notePageRecyclerView == null || mBinding.notePageRecyclerView.getPaginator() == null) {
            return;
        }
        GPaginator paginator = mBinding.notePageRecyclerView.getPaginator();
        mManagerViewModel.setPageStatus(paginator.getVisibleCurrentPage(),
                paginator.pages());
    }

    private static class NoteItemViewHolder extends BindingViewHolder<NoteItemBinding, ManagerItemViewModel> {
        NoteItemViewHolder(NoteItemBinding binding) {
            super(binding);
        }

        public void bindTo(ManagerItemViewModel model) {
            mBinding.setViewModel(model);
            mBinding.executePendingBindings();
        }
    }

    private void buildAdapter() {
        mAdapter = new ManagerAdapter(this);
    }

    public static class ManagerAdapter extends PageAdapter<NoteItemViewHolder, NoteModel, ManagerItemViewModel> {
        private ManagerActivity mNoteItemNavigator;
        private LayoutInflater mLayoutInflater;
        /*
        * TODO:Because PageRecyclerView need it's own notifyDataSetChanged() (not the adapter one)to update page status.
        * so we had to obtain a fragment weakReference (avoid leak)to update page info text when first load.
        * Maybe OnPagingListener should always trigger when data load into view,which we didn't
        * need to update some page info text manually for first time loading.
        */
        private WeakReference<ManagerFragment> fragmentWeakReference;

        ManagerAdapter(ManagerFragment fragment) {
            mNoteItemNavigator = (ManagerActivity) fragment.getActivity();
            mLayoutInflater = mNoteItemNavigator.getLayoutInflater();
            fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public int getRowCount() {
            return 3;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public NoteItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            return new NoteItemViewHolder(NoteItemBinding.inflate(mLayoutInflater, parent, false));
        }

        @Override
        public void onPageBindViewHolder(NoteItemViewHolder holder, int position) {
            holder.bindTo(getItemVMList().get(position));
        }

        @Override
        public void setRawData(List<NoteModel> rawData, Context context) {
            super.setRawData(rawData, context);
            for (NoteModel model : rawData) {
                ManagerItemViewModel viewModel = new ManagerItemViewModel(
                        context);
                viewModel.setNote(model);
                viewModel.setNavigator(mNoteItemNavigator);
                getItemVMList().add(viewModel);
            }
            if (fragmentWeakReference.get() != null) {
                fragmentWeakReference.get().mBinding.notePageRecyclerView.notifyDataSetChanged();
                fragmentWeakReference.get().updatePageStatus();
            }
        }
    }
}