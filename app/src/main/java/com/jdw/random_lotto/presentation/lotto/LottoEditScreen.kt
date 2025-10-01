package com.jdw.random_lotto.presentation.lotto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdw.random_lotto.common.util.LottoType

@Composable
fun LottoEditScreen(selectedTab: LottoType, lottoVm: LottoViewModel) {

    val cs = colorScheme

    val state = lottoVm.state.collectAsStateWithLifecycle()

    // 탭별 아이템만 필터 (계산 최소화)
    val lottoItems by remember(state.value.insertItems, selectedTab) {
        derivedStateOf { state.value.insertItems.filter { it.type == selectedTab } }
    }

    val deselectedNumbers = rememberSaveable(selectedTab) { mutableStateListOf<String>() }

    // 리스트가 바뀔 때, 더 이상 존재하지 않는 번호는 해제 집합에서 정리
    LaunchedEffect(lottoItems) {
        val cur = lottoItems.map { it.number }.toSet()
        deselectedNumbers.retainAll(cur) // 사라진 항목 정리
    }

    val selectBoxColor = cs.primaryContainer
    val unSelectBoxBorder = cs.onSurface

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                lottoVm.dispatch(LottoIntent.Edit(selectedTab))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp) ,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(text = "무작위 생성")
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(lottoItems) { item ->
                val isSelected = item.number !in deselectedNumbers

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .then(
                            if (!isSelected) Modifier.border(
                                width = 1.dp,
                                color = unSelectBoxBorder,
                                shape = RoundedCornerShape(8.dp)
                            ) else Modifier
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) selectBoxColor
                            else cs.background
                        )
                        .clickable {
                            if (isSelected) deselectedNumbers.add(item.number)
                            else deselectedNumbers.remove(item.number)
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.number
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = cs.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
