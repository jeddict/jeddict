/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jeddict.test.mock;

import io.github.jeddict.jcode.task.ITaskSupervisor;

/**
 *
 * @author jGauravGupta
 */
public class MockTaskSupervisor implements ITaskSupervisor {

    @Override
    public boolean start(int itemTotal) {
        return true;
    }

    @Override
    public boolean start(int contributor, int itemTotal) {
        return true;
    }

    @Override
    public int increment() {
        return 1;
    }

    @Override
    public int increment(int step) {
        return 1;
    }

    @Override
    public boolean proceed() {
        return true;
    }

    @Override
    public boolean proceed(int step) {
        return true;
    }

    @Override
    public void log() {

    }

    @Override
    public void log(int level) {

    }

    @Override
    public void log(String msg) {
        System.out.print(msg);
    }

    @Override
    public void log(int level, String msg) {
        log(msg);
    }

    @Override
    public void log(String msg, boolean newline) {
        if (newline) {
            System.out.println(msg);
        } else {
            System.out.print(msg);
        }
    }

    @Override
    public void log(String msg, int padding) {
        log(msg);
    }

    @Override
    public void log(int level, String msg, boolean newline) {
        if (newline) {
            System.out.println(msg);
        } else {
            System.out.print(msg);
        }
    }

    @Override
    public boolean cancel() {
        return true;
    }

    @Override
    public void fail() {

    }
}
