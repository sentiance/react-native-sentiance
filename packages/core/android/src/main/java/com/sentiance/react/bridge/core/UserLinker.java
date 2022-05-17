package com.sentiance.react.bridge.core;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;


public class UserLinker implements com.sentiance.sdk.UserLinker {

    private final SentianceEmitter mSentianceEmitter;
    private boolean mUserLinkResult;
    private CountDownLatch mCountDownLatch;

    public UserLinker (SentianceEmitter sentianceEmitter) {
        mSentianceEmitter = sentianceEmitter;
    }

    @Override
    public boolean link(@NonNull String installId) {
        mCountDownLatch = new CountDownLatch(1);
        mSentianceEmitter.sendUserLinkEvent(installId);
        try {
            mCountDownLatch.await();
            return mUserLinkResult;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void setUserLinkResult(boolean userLinkResult) {
        mUserLinkResult = userLinkResult;
        mCountDownLatch.countDown();
    }
}
