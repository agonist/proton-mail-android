/*
 * Copyright (c) 2020 Proton Technologies AG
 * 
 * This file is part of ProtonMail.
 * 
 * ProtonMail is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ProtonMail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ProtonMail. If not, see https://www.gnu.org/licenses/.
 */
package ch.protonmail.android.events.user;

import ch.protonmail.android.api.models.UserSettings;

/**
 * Created by dino on 10/10/17.
 */

public class UserSettingsEvent {

    private UserSettings mUserSettings;

    public UserSettingsEvent(UserSettings userSettings) {
        this.mUserSettings = userSettings;
    }

    public UserSettings getUserSettings() {
        return mUserSettings;
    }
}
