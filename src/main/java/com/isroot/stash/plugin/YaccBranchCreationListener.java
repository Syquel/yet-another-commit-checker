package com.isroot.stash.plugin;

import java.util.List;
import javax.annotation.Nonnull;

import com.atlassian.bitbucket.event.branch.BranchCreationHookRequest;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.setting.Settings;
import com.isroot.stash.plugin.checks.BranchNameCheck;
import com.isroot.stash.plugin.errors.YaccError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hiroyuki Wada
 */
public class YaccBranchCreationListener implements PreRepositoryHook<BranchCreationHookRequest> {

    private static final Logger log = LoggerFactory.getLogger(YaccBranchCreationListener.class);
    private final I18nService i18nService;

    public YaccBranchCreationListener(final I18nService i18nService) {
        this.i18nService = i18nService;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(
        @Nonnull final PreRepositoryHookContext preRepositoryHookContext,
        @Nonnull final BranchCreationHookRequest branchCreationHookRequest
    ) {
        final Settings settings = preRepositoryHookContext.getSettings();
        final String branchId = branchCreationHookRequest.getBranch().getId();

        List<YaccError> errors = new BranchNameCheck(settings, branchId).check();

        RepositoryHookResult result;
        if (!errors.isEmpty()) {
            result = RepositoryHookResult.rejected("Invalid branch name", i18nService.getKeyedText("invalidBranchName", errors.get(0).getMessage()).getLocalisedMessage());
        } else {
            result = RepositoryHookResult.accepted();
        }

        return result;
    }
}
