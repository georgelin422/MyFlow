/*
 * Copyright 2014 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myapp.myflow;

import android.app.Application;

import com.google.gson.Gson;
import com.myapp.myflow.util.FlowBundler;

import javax.annotation.Nullable;

import dagger.ObjectGraph;
import flow.Backstack;
import timber.log.Timber;

public class DemoApp extends Application {
    private final FlowBundler flowBundler = new FlowBundler(new GsonParceler(new Gson())) {
        @Override protected Backstack getColdStartBackstack(@Nullable Backstack restoredBackstack) {
            return restoredBackstack == null ? Backstack.single(new Paths.ConversationList())
                : restoredBackstack;
        }
    };
    private ObjectGraph globalGraph;

    @Override public void onCreate() {
        super.onCreate();

        globalGraph = ObjectGraph.create(new DaggerConfig());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    public FlowBundler getFlowBundler() {
        return flowBundler;
    }

    public ObjectGraph getGlobalGraph() {
        return globalGraph;
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override public void i(String message, Object... args) {
            // TODO e.g., Crashlytics.log(String.format(message, args));
        }

        @Override public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override public void e(Throwable t, String message, Object... args) {
            e(message, args);

            // TODO e.g., Crashlytics.logException(t);
        }
    }
}
