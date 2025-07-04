/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apicurio.registry.core;

import io.apicurio.common.apps.config.Info;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static io.apicurio.common.apps.config.ConfigPropertyCategory.CATEGORY_SYSTEM;

/**
 * @author eric.wittmann@gmail.com
 */
@ApplicationScoped
public class System {

    @Inject
    @ConfigProperty(name = "apicurio.app.name")
    @Info(category = CATEGORY_SYSTEM, registryAvailableSince = "3.0.4")
    String name;

    @Inject
    @ConfigProperty(name = "apicurio.app.description")
    @Info(category = CATEGORY_SYSTEM, registryAvailableSince = "3.0.4")
    String description;

    @Inject
    @ConfigProperty(name = "apicurio.app.version")
    @Info(category = CATEGORY_SYSTEM, registryAvailableSince = "3.0.4")
    String version;

    @Inject
    @ConfigProperty(name = "apicurio.app.date")
    @Info(category = CATEGORY_SYSTEM, registryAvailableSince = "3.0.4")
    String date;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return this.version;
    }

    /**
     * @return the versionDate
     */
    public Date getDate() {
        try {
            if (date == null) {
                return new Date();
            } else {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
            }
        } catch (ParseException e) {
            return new Date();
        }
    }

}
