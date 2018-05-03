package net.hailm.firebaseapp.listener;

import net.hailm.firebaseapp.model.dbmodels.CommentModel;

public interface CommentApdaterCallback {
    void onLongItemClick(CommentModel commentModel);
}

