package com.ly.supermvp.ui.fragment;

import android.view.View;

import com.ly.supermvp.R;
import com.ly.supermvp.adapter.NewsListAdapter;
import com.ly.supermvp.delegate.NewsFragmentDelegate;
import com.ly.supermvp.model.NewsModel;
import com.ly.supermvp.model.NewsModelImpl;
import com.ly.supermvp.model.entity.NewsBody;
import com.ly.supermvp.mvp_frame.presenter.FragmentPresenter;
import com.ly.supermvp.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <Pre>
 * 新闻fragment
 * </Pre>
 *
 * @author 刘阳
 * @version 1.0
 *          <p/>
 *          Create by 2016/1/27 11:04
 */
public class NewsFragment extends FragmentPresenter<NewsFragmentDelegate> implements NewsFragmentDelegate.SwipeRefreshAndLoadMoreCallBack {
    private NewsModel mNewsModel;
    private int mPageNum = 1;
    private NewsListAdapter mAdapter;

    //新闻数据列表
    private List<NewsBody> mNews = new ArrayList<>();

//    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    protected Class<NewsFragmentDelegate> getDelegateClass() {
        return NewsFragmentDelegate.class;
    }


    @Override
    protected void initData() {
        super.initData();
        ToastUtils.register(getActivity());
        mNewsModel = new NewsModelImpl();

        mAdapter = new NewsListAdapter(getActivity(), mNews);
        viewDelegate.setListAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ToastUtils.showShort("暂时不支持查看详情");
            }
        });

        //注册下拉刷新
        viewDelegate.registerSwipeRefreshCallBack(this);
        //注册加载更多
        viewDelegate.registerLoadMoreCallBack(this, mAdapter);

        netNewsList(true);
    }

    /**
     * 从网络加载数据列表
     * @param isRefresh 是否刷新
     */
    private void netNewsList(final boolean isRefresh) {
//        viewDelegate.showLoading();
        if(isRefresh){
            mPageNum = 1;
        }else {
            mPageNum++;
        }
        mNewsModel.netLoadNewsList(mPageNum, NewsModelImpl.CHANNEL_ID, NewsModelImpl.CHANNEL_NAME, new NewsModel.OnLoadNewsListListener() {
            @Override
            public void onSuccess(List<NewsBody> list) {
                viewDelegate.showContent();
                if(isRefresh) {
                    if(!mNews.isEmpty()){
                        mNews.clear();
                    }
                }
                mNews.addAll(list);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                viewDelegate.showError(R.string.load_error, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        netNewsList(true);
                    }
                });
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
    }

    /**
     * 下拉刷新
     */
    @Override
    public void refresh() {
        netNewsList(true);
    }

    /**
     * 加载更多
     */
    @Override
    public void loadMore() {
        netNewsList(false);
    }
}
