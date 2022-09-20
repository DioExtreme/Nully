package com.dioextreme.nully.module.tally.setup.menu;

public class TallyMenuInteraction
{
    // Interaction that destroys MenuListener
    public static final int EXIT = -1;
    public static final int MAIN = 0;
    public static final int MILESTONE = 1;
    public static final int MILESTONE_ADD = 2;
    public static final int MILESTONE_EDIT = 3;
    public static final int MILESTONE_REMOVE = 4;
    public static final int MILESTONE_ADD_CONFIRM_NO = 5;
    public static final int MILESTONE_EDIT_CONFIRM_NO = 6;
    public static final int MILESTONE_REMOVE_CONFIRM_NO = 7;
    public static final int MODE = 8;
    public static final int MODE_ADD = 9;
    public static final int MODE_EDIT = 10;
    public static final int MODE_REMOVE = 11;
    public static final int MODE_ADD_CONFIRM_NO = 12;
    public static final int MODE_EDIT_CONFIRM_NO = 13;
    public static final int MODE_REMOVE_CONFIRM_NO = 14;


    // Interactions the same Menu object has to handle
    public static final int INIT = 1000;
    public static final int RUN_CHANNEL_SELECT = 1001;
    public static final int LOG_CHANNEL_SELECT = 1002;
    public static final int CONFIRM_INIT = 1003;
    public static final int MILESTONE_ADD_MODAL = 1004;
    public static final int MILESTONE_ADD_CONFIRM_YES = 1005;
    public static final int MILESTONE_EDIT_TRIGGER_SELECT = 1006;
    public static final int MILESTONE_EDIT_MODAL = 1007;
    public static final int MILESTONE_EDIT_CONFIRM_YES = 1008;
    public static final int MILESTONE_REMOVE_TRIGGER_SELECT = 1009;
    public static final int MILESTONE_REMOVE_CONFIRM_YES = 1010;
    public static final int MODE_ADD_MODAL = 1011;
    public static final int MODE_ADD_CONFIRM_YES = 1012;
    public static final int MODE_EDIT_SELECT = 1013;
    public static final int MODE_EDIT_MODAL = 1014;
    public static final int MODE_EDIT_CONFIRM_YES = 1015;
    public static final int MODE_REMOVE_SELECT = 1016;
    public static final int MODE_REMOVE_CONFIRM_YES = 1017;


    // Intermediate interactions, unreachable by the user
    public static final int MILESTONE_ADD_CONFIRM = 10000;
    public static final int MILESTONE_EDIT_CONFIRM = 10001;
    public static final int MODE_ADD_CONFIRM = 10002;
    public static final int MODE_EDIT_CONFIRM = 10003;
}
