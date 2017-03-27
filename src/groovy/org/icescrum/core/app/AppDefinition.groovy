/*
 * Copyright (c) 2017 Kagilum SAS
 *
 * This file is part of iceScrum.
 *
 * iceScrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * iceScrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with iceScrum.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Authors:
 *
 * Nicolas Noullet (nnoullet@kagilum.com)
 * Vincent Barrier (vbarrier@kagilum.com)
 *
 */
package org.icescrum.core.app

import grails.util.Holders

class AppDefinition {

    boolean hasWidgets = false
    boolean hasWindows = false
    boolean isProject = false
    boolean isServer = false
    String id
    String logo
    String version
    String author
    String docUrl
    String websiteUrl
    List<String> screenshots = []
    List<String> tags = []
    Closure onEnableForProject
    Closure onDisableForProject
    AppSettingsDefinition projectSettings

    // Builder

    void logo(String logo) {
        this.logo = logo
    }

    void version(String version) {
        this.version = version
    }

    void author(String author) {
        this.author = author
    }

    void docUrl(String docUrl) {
        this.docUrl = docUrl
    }

    void websiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl
    }

    void screenshots(String... screenshots) {
        this.screenshots.addAll(screenshots)
    }

    void tags(String... tags) {
        this.tags.addAll(tags)
        this.tags.unique()
    }

    void hasWidgets(boolean hasWidgets) {
        this.hasWidgets = hasWidgets
    }

    void hasWindows(boolean hasWindows) {
        this.hasWindows = hasWindows
    }

    void isProject(boolean isProject) {
        this.isProject = isProject
    }

    void isServer(boolean isServer) {
        this.isServer = isServer
    }

    void onEnableForProject(Closure onEnableForProject) {
        this.onEnableForProject = onEnableForProject
    }

    void onDisableForProject(Closure onDisableForProject) {
        this.onDisableForProject = onDisableForProject
    }

    void projectSettings(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=AppSettingsDefinition) Closure settingsClosure) {
        AppSettingsDefinition projectSettings = new AppSettingsDefinition()
        AppDefinitionsBuilder.builObjectFromClosure(projectSettings, settingsClosure, this)
        this.projectSettings = projectSettings
    }

    // Utility

    static Map getAttributes(AppDefinition appDefinition) {
        def attributes = appDefinition.properties.clone()
        ['class', 'onDisableForProject', 'onEnableForProject'].each { k ->
            attributes.remove(k)
        }
        def g = Holders.grailsApplication.mainContext.getBean("org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib")
        ['name', 'baseline', 'description'].each { k ->
            attributes[k] = g.message(code: 'is.ui.apps.' + appDefinition.id + '.'+ k)
        }
        return attributes
    }

    Map validate() {
        def result = [valid: false, errorMessage: "Error, this app definition cannot be registered: $id"]
        if (!id || !docUrl || !version || !author) {
            result.errorMessage += '\n - These fields are required: docUrl, version, author'
        } else if ((onEnableForProject || onDisableForProject || projectSettings) && !isProject) {
            result.errorMessage += '\n - The fields onEnableForProject, onDisableForProject and projectSettings can be defined only if isProject is true'
        } else if (!isProject && !isServer) {
            result.errorMessage += '\n - At least one of the fields isProject and isServer must be true (both can be true)'
        } else {
            result.valid = true;
        }
        return result
    }
}