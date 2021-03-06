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

package ch.protonmail.android.uitests.testsHelper.uiactions

import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.typeText

fun ViewInteraction.click(): ViewInteraction =
    this.perform(ViewActions.click())

fun ViewInteraction.insert(text: String): ViewInteraction =
    this.perform(replaceText(text), closeSoftKeyboard())

fun ViewInteraction.type(text: String): ViewInteraction =
    this.perform(typeText(text), closeSoftKeyboard())

fun ViewInteraction.swipeViewDown(): ViewInteraction =
    this.perform(swipeDown())
