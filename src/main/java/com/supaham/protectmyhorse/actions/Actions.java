package com.supaham.protectmyhorse.actions;

public class Actions {

    public interface Action {

        boolean requiresTaming();
    }

    public static abstract class PlayerAction implements Action {

        public abstract String getPlayerName();
    }

    public static abstract class Lock implements Action {

        @Override
        public boolean requiresTaming() {

            return true;
        }
    }

    public static abstract class Unlock implements Action {

        @Override
        public boolean requiresTaming() {

            return true;
        }
    }

    public static abstract class Type implements Action {

        public abstract com.supaham.protectmyhorse.protection.ProtectedHorse.Type getType();

        @Override
        public boolean requiresTaming() {

            return true;
        }
    }

    public static abstract class Info implements Action {

        @Override
        public boolean requiresTaming() {
        
            return false;
        }
    }

    public static abstract class Add extends PlayerAction {

        @Override
        public boolean requiresTaming() {

            return true;
        }
    }

    public static abstract class Remove extends PlayerAction {

        @Override
        public boolean requiresTaming() {

            return true;
        }
    }
}
