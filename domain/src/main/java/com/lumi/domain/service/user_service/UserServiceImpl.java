package com.lumi.domain.service.user_service;

import com.lumi.domain.model.user.User;
import com.lumi.domain.repository.ProjectRepository;
import com.lumi.domain.repository.UserRepository;
import com.lumi.domain.utils.ApiUtils;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class UserServiceImpl implements UserService {

    private UserRepository mUserServerRepository;
    private UserRepository mUserDbRepository;

    @Inject
    public UserServiceImpl(UserRepository userServerRepository, UserRepository userDbRepository){
        mUserDbRepository = userDbRepository;
        mUserServerRepository = userServerRepository;
    }

    @Override
    public Single<User> getUser(String username) {
        return mUserServerRepository.getUser(username)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(mUserDbRepository::insertUser)
                .onErrorReturn(throwable -> ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.getClass())
                        ? mUserDbRepository.getUser(username).blockingGet() : null);
    }

    @Override
    public void insertUser(User user) {
        mUserDbRepository.insertUser(user);
    }
}
