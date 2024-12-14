package com.adityaputra.sholatreminder

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class PrayerWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String = "SholatReminder"

    override fun getDisplayName(): String = "Sholat Reminder"

    override fun isAvailable(project: Project): Boolean = true

    override fun createWidget(project: Project): StatusBarWidget {
        return PrayerStatusBarWidget()
    }

    override fun disposeWidget(widget: StatusBarWidget) {}

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}