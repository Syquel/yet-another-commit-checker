package com.isroot.stash.plugin;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nonnull;

import com.atlassian.bitbucket.scope.Scope;
import com.atlassian.bitbucket.setting.Settings;
import com.atlassian.bitbucket.setting.SettingsValidationErrors;
import com.atlassian.bitbucket.setting.SettingsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author sdford
 * @since 2013-05-11
 */
public class ConfigValidator implements SettingsValidator {
    private static final Logger log = LoggerFactory.getLogger(ConfigValidator.class);

    private final JiraService jiraService;

    public ConfigValidator(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @Override
    public void validate(@Nonnull final Settings settings, @Nonnull final SettingsValidationErrors settingsValidationErrors, @Nonnull final Scope scope) {
        validationRegex(settings, settingsValidationErrors, "commitMessageRegex");
        validationRegex(settings, settingsValidationErrors, "committerEmailRegex");
        validationRegex(settings, settingsValidationErrors, "excludeByRegex");
        validationRegex(settings, settingsValidationErrors, "excludeBranchRegex");
        validationRegex(settings, settingsValidationErrors, "branchNameRegex");

        if (settings.getBoolean("requireJiraIssue", false)) {
            if (!jiraService.doesJiraApplicationLinkExist()) {
                settingsValidationErrors.addFieldError("requireJiraIssue", "Can't be enabled because a JIRA application link does not exist.");
            }
        }

        String jqlMatcher = settings.getString("issueJqlMatcher");
        if (!isNullOrEmpty(jqlMatcher)) {
            List<String> jqlErrors = jiraService.checkJqlQuery(jqlMatcher);
            for (String err : jqlErrors) {
                settingsValidationErrors.addFieldError("issueJqlMatcher", err);
            }
        }
    }

    private void validationRegex(Settings settings,
                                 SettingsValidationErrors errors,
                                 String setting) {
        String regex = settings.getString(setting);
        if (regex != null && !regex.isEmpty()) {
            try {
                Pattern.compile(regex);
            } catch (PatternSyntaxException ex) {
                errors.addFieldError(setting, "Invalid Regex: " + ex.getMessage());
            }
        }

    }

}
