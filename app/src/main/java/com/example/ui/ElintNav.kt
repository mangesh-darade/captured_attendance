package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.lang.LangManager
import com.example.ui.theme.ElintBrandPrimary
import com.example.ui.theme.ElintDimens
import com.example.ui.theme.ElintDivider
import com.example.ui.theme.ElintSurface
import com.example.ui.theme.ElintText
import com.example.ui.theme.ElintTextMuted

enum class ElintTab { HOME, MY_DAY, RECORDS, PROFILE }

/**
 * Bottom bar of the companion app: Home, My day, Records, Profile. 76dp, white, 1dp top border;
 * the active tab is primary and bold.
 */
@Composable
fun ElintBottomNav(selected: ElintTab, language: String, onSelect: (ElintTab) -> Unit) {
    val s = LangManager.getStrings(language)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ElintSurface,
        border = BorderStroke(1.dp, ElintDivider)
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .heightIn(min = ElintDimens.bottomNavHeight)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(painterResource(R.drawable.ic_home), s.homeTab, selected == ElintTab.HOME, Modifier.weight(1f)) { onSelect(ElintTab.HOME) }
            NavItem(painterResource(R.drawable.ic_calendar), s.myDayTab, selected == ElintTab.MY_DAY, Modifier.weight(1f)) { onSelect(ElintTab.MY_DAY) }
            NavItem(painterResource(R.drawable.ic_records), s.recordsTab, selected == ElintTab.RECORDS, Modifier.weight(1f)) { onSelect(ElintTab.RECORDS) }
            NavItem(painterResource(R.drawable.ic_profile), s.profileTab, selected == ElintTab.PROFILE, Modifier.weight(1f)) { onSelect(ElintTab.PROFILE) }
        }
    }
}

@Composable
private fun NavItem(icon: Painter, label: String, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val tint = if (active) ElintBrandPrimary else ElintTextMuted
    Column(
        modifier = modifier.heightIn(min = ElintDimens.minTouchTarget).clickable { onClick() }.padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(ElintDimens.iconLarge))
        Text(label, style = ElintText.navLabel.copy(fontWeight = if (active) FontWeight.Bold else FontWeight.Medium), color = tint)
    }
}
