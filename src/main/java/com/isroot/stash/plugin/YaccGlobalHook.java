package com.isroot.stash.plugin;

import javax.annotation.Nonnull;

import com.atlassian.bitbucket.hook.repository.PreRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.RepositoryHookService;
import com.atlassian.bitbucket.hook.repository.RepositoryPushHookRequest;
import com.atlassian.bitbucket.setting.Settings;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * System-wide pre-receive hook.  Will defer to the local repository YACC hook configuration if present.
 *
 * @author Uldis Ansmits
 * @author Jim Bethancourt
 */
public class YaccGlobalHook implements PreRepositoryHook<RepositoryPushHookRequest> {

    private static final Logger log = LoggerFactory.getLogger(YaccGlobalHook.class);

    private final YaccService yaccService;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final SecurityService securityService;
    private final RepositoryHookService repositoryHookService;

    public YaccGlobalHook(YaccService yaccService,
                              PluginSettingsFactory pluginSettingsFactory,
                              SecurityService securityService,
                              RepositoryHookService repositoryHookService) {
        this.yaccService = yaccService;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.securityService = securityService;
        this.repositoryHookService = repositoryHookService;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(
        @Nonnull PreRepositoryHookContext context,
        @Nonnull RepositoryPushHookRequest repositoryPushHookRequest
    ) {
        final Settings settings = context.getSettings();
        return yaccService.check(context, repositoryPushHookRequest, settings);
    }

}
