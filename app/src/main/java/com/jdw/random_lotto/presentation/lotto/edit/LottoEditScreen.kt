package com.jdw.random_lotto.presentation.lotto.edit

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.NavigationMode
import com.jdw.random_lotto.common.util.permission.AppPermission
import com.jdw.random_lotto.common.util.permission.PermissionManager
import com.jdw.random_lotto.presentation.lotto.edit.components.LottoItem
import com.jdw.random_lotto.presentation.lotto.result.LottoResultEffect

/**
 * 복권 추가 화면
 * @param selectedTab - 현재 선택된 탭 (복권 종류)
 * @param vm - 뷰모델
 * @param onNavigate - 네비게이트 콜백
 */
@Composable
fun LottoEditScreen(
    selectedTab: LottoType,
    vm: LottoEditViewModel,
    onNavigate: (NavigationMode) -> Unit,
    onSnackBar: (String) -> Unit
) {
    val cs = colorScheme
    val state by vm.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // 탭별 아이템만
    val lottoItems by remember(state.insertItems, selectedTab) {
        derivedStateOf { state.insertItems.filter { it.type == selectedTab } }
    }

    // 탭별 선택 해제된 키
    val deselected: Set<String> = state.deselectedKeysByType.getValue(selectedTab)

    // 선택 개수 계산
    val keys by remember(lottoItems) { derivedStateOf { lottoItems.map { it.signature } } }
    val selectedCount by remember(keys, deselected) {
        derivedStateOf { keys.size - deselected.count { it in keys.toSet() } }
    }

    // 카메라 퍼미션 런처
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val allGranted = result.values.all { it }
            if (allGranted) {
                onNavigate(NavigationMode.QR_SCAN)
            } else {

            }
        }

    // 갤러리 퍼미션 런처
    val galleryPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val allGranted = result.values.all { it }
            if (allGranted) {
                // 권한 허용되면 실제 갤러리 열기
                onNavigate(NavigationMode.QR_GALLERY)
            } else {
                // 거부된 경우 → 필요하면 토스트/스낵바 등 처리
            }
        }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is LottoEditEffect.ShowSnackbar -> {
                    onSnackBar(effect.message)
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {

            // 상단 고정 액션줄
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cs.background)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 무작위 생성 버튼
                    OutlinedButton(
                        onClick = { vm.dispatch(LottoEditIntent.Edit(selectedTab)) },
                        border = BorderStroke(1.dp, cs.primary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = cs.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            Icons.Filled.Casino,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("무작위 생성")
                    }

                    // 갤러리 QR 버튼
                    OutlinedButton(
                        onClick = {
                            PermissionManager.checkAppPermission(
                                context = context,
                                activity = context as Activity,
                                permission = AppPermission.GALLERY,
                                active = {
                                    onNavigate(NavigationMode.QR_GALLERY)
                                },
                                lancher = { perms ->
                                    galleryPermissionLauncher.launch(perms)
                                }
                            )
                        },
                        border = BorderStroke(1.dp, cs.primary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = cs.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            Icons.Filled.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("갤러리 QR")
                    }

                    Spacer(Modifier.weight(1f))

                    // 전체 선택/해제
                    IconButton(onClick = {
                        vm.dispatch(LottoEditIntent.SelectAll(selectedTab, keys.toSet()))
                    }) {
                        Icon(
                            if (deselected.isEmpty()) Icons.Filled.RadioButtonChecked
                            else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = cs.primary
                        )
                    }
                }
            }

            // 리스트
            items(
                items = lottoItems,
                key = { it.signature }
            ) { item ->
                val key = remember(item) { item.signature }
                val selected = key !in deselected

                LottoItem(
                    item = item,
                    selected = selected,
                    onToggle = {
                        vm.dispatch(LottoEditIntent.ToggleSelect(selectedTab, key))
                    }
                )
            }
        }

        // 선택된 아이템 저장, 삭제 버튼
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    vm.dispatch(LottoEditIntent.Delete(selectedTab))
                },
                enabled = selectedCount > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cs.error,
                    contentColor = cs.onPrimary,
                    disabledContainerColor = cs.errorContainer,
                    disabledContentColor = cs.onPrimaryContainer
                ),
                shape = RoundedCornerShape(14.dp)
            ) { Text("선택 ${selectedCount}개 제거") }

            Button(
                onClick = {
                    vm.dispatch(LottoEditIntent.Save(selectedTab))
                },
                enabled = selectedCount > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cs.primary,
                    contentColor = cs.onPrimary,
                    disabledContainerColor = cs.primaryContainer,
                    disabledContentColor = cs.onPrimaryContainer
                ),
                shape = RoundedCornerShape(14.dp)
            ) { Text("선택 ${selectedCount}개 저장") }

        }

        // QR 스캔 플로팅 액션 버튼
        ExtendedFloatingActionButton(
            onClick = {
                PermissionManager.checkAppPermission(
                    context = context,
                    activity = context as Activity,
                    permission = AppPermission.CAMERA,
                    active = { onNavigate(NavigationMode.QR_SCAN) },
                    lancher = { perms ->
                        cameraPermissionLauncher.launch(perms)
                    }
                )
            },
            icon = { Icon(Icons.Filled.QrCodeScanner, contentDescription = null) },
            text = { Text("QR 스캔") },
            containerColor = cs.primary,
            contentColor = cs.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 84.dp)
        )
    }
}
