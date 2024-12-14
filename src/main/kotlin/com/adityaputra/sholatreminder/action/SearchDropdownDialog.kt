package com.adityaputra.sholatreminder.action
import com.adityaputra.sholatreminder.model.DropdownItem
import com.adityaputra.sholatreminder.service.CityStateService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SearchDropdownDialog<T>(
    private val title: String,
    private val items: List<DropdownItem<T>>,
    private val onItemSelected: (DropdownItem<T>) -> Unit
) {
    private val listModel = DefaultListModel<DropdownItem<T>>()
    private val list: JBList<DropdownItem<T>>
    private val searchField: SearchTextField
    private val panel: JPanel

    init {
        // Initialize the list model
        items.forEach { listModel.addElement(it) }

        // Create the main panel
        panel = createMainPanel()
        searchField = createSearchField()
        list = createList()

        setupLayout()
        setupSearchFunctionality()
        setupListSelection()
        setupKeyboardNavigation()
    }

    private var highlightedIndex: Int = -1

    private fun setupKeyboardNavigation() {
        searchField.textEditor.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_DOWN -> {
                        if (highlightedIndex < list.model.size - 1) {
                            highlightedIndex++
                            list.selectedIndex = highlightedIndex
                            list.ensureIndexIsVisible(highlightedIndex)
                        }
                    }
                    KeyEvent.VK_UP -> {
                        if (highlightedIndex > 0) {
                            highlightedIndex--
                            list.selectedIndex = highlightedIndex
                            list.ensureIndexIsVisible(highlightedIndex)
                        }
                    }
                    KeyEvent.VK_ENTER -> {
                        list.selectedValue?.let { selected ->
                            onItemSelected(selected)
                            popup?.cancel()
                        }
                    }
                }
            }
        })
    }

    private fun createMainPanel(): JPanel {
        val outerPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(16)
        }

        val contentPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(10)
        }

        outerPanel.add(contentPanel, BorderLayout.CENTER)
        return outerPanel
    }

    private fun createSearchField(): SearchTextField {
        return SearchTextField().apply {
            textEditor.border = JBUI.Borders.empty(10)
            preferredSize = Dimension(400, 40)
        }
    }

    private fun createList(): JBList<DropdownItem<T>> {
        return JBList(listModel).apply {
            setEmptyText("No matching items")
            border = JBUI.Borders.empty(2, 10)
            cellRenderer = object : DefaultListCellRenderer() {
                override fun getListCellRendererComponent(
                    list: JList<*>?,
                    value: Any?,
                    index: Int,
                    isSelected: Boolean,
                    cellHasFocus: Boolean
                ): Component {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                    val item = value as DropdownItem<*>
                    text = item.label
                    border = JBUI.Borders.empty(2, 4)
                    return this
                }
            }
        }
    }

    private fun setupLayout() {
        val contentPanel = panel.getComponent(0) as JPanel

        val searchPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.emptyBottom(2)
            add(searchField, BorderLayout.CENTER)
        }

        contentPanel.add(searchPanel, BorderLayout.NORTH)
        val scrollPane = JBScrollPane(list)
        contentPanel.add(scrollPane, BorderLayout.CENTER)
    }

    private fun setupSearchFunctionality() {
        searchField.textEditor.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = filterList()
            override fun removeUpdate(e: DocumentEvent) = filterList()
            override fun changedUpdate(e: DocumentEvent) = filterList()

            private fun filterList() {
                val searchText = searchField.text.lowercase()
                val filteredModel = DefaultListModel<DropdownItem<T>>()
                    val filteredItems = items.filter {
                        it.label.lowercase().contains(searchText)
                    }
                filteredItems.forEach { filteredModel.addElement(it) }
                list.model = filteredModel
            }
        })
    }

    private fun setupListSelection() {
        list.addListSelectionListener {
            if (!it.valueIsAdjusting && popup?.isDisposed == true) {
                list.selectedValue?.let { selected ->
                    onItemSelected(selected)
//                    popup?.cancel()
                }
            }
        }
        list.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 1) { // Pastikan klik tunggal
                    val index = list.locationToIndex(e.point)
                    if (index >= 0 && index < list.model.size) {
                        list.selectedIndex = index // Pilih item yang di-klik
                        list.selectedValue?.let { selected ->
                            onItemSelected(selected)
                            popup?.cancel()
                        }
                    }
                }
            }
        })
    }

    private var popup: JBPopup? = null

    fun show(project: Project) {
        popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, searchField)
            .setTitle(title)
            .setMovable(false)
            .setResizable(true)
            .setRequestFocus(true)
            .createPopup()

        popup?.showCenteredInCurrentWindow(project)
    }
}
