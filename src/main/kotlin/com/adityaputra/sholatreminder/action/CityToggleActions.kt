package com.adityaputra.sholatreminder.action

import com.adityaputra.sholatreminder.service.CityStateService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBCheckBox
import javax.swing.JOptionPane


class CityToggleActions : ToggleAction() {
    val locService = service<CityStateService>()
    override fun isSelected(e: AnActionEvent): Boolean {
        return  locService.isShow()
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        locService.state.isShow = state
    }
}