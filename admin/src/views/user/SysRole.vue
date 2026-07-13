<template>
  <div class="app-container">
    <el-card>
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-4">
          <el-input v-model="searchForm.keyword" placeholder="搜索角色名称或编码" clearable style="width:250px" @keyup.enter="loadData" />
          <el-button type="primary" @click="loadData">搜索</el-button>
        </div>
        <el-button type="success" @click="showAddModal">新增角色</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="roleName" label="角色名称" width="120" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="角色描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="showEditModal(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        class="mt-4"
      />
    </el-card>

    <el-dialog :title="isAdd ? '新增角色' : '编辑角色'" v-model="showModal" width="450px">
      <el-form :model="form" label-width="100px" class="mt-4">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码（如：ADMIN）" />
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input v-model="form.description" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model="form.sortOrder" type="number" placeholder="请输入排序号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ isAdd ? '确认添加' : '确认修改' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { listRole, addRole, updateRole, deleteRole } from '@/api';
const tableData = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const showModal = ref(false);
const isAdd = ref(true);
const searchForm = reactive({
 keyword: ''
});
const form = reactive({
 id: null,
 roleName: '',
 roleCode: '',
 description: '',
 status: '1',
 sortOrder: 0
});
const loadData = () => {
 listRole(pageNum.value, pageSize.value, searchForm.keyword).then(res => {
 if (res.code === 200) {
 tableData.value = res.data.records;
 total.value = res.data.total;
 }
 });
};
const showAddModal = () => {
 isAdd.value = true;
 form.id = null;
 form.roleName = '';
 form.roleCode = '';
 form.description = '';
 form.status = '1';
 form.sortOrder = 0;
 showModal.value = true;
};
const showEditModal = (row) => {
 isAdd.value = false;
 form.id = row.id;
 form.roleName = row.roleName;
 form.roleCode = row.roleCode;
 form.description = row.description;
 form.status = String(row.status);
 form.sortOrder = row.sortOrder;
 showModal.value = true;
};
const handleSubmit = () => {
 if (!form.roleName) {
 ElMessage.error('请输入角色名称');
 return;
 }
 if (!form.roleCode) {
 ElMessage.error('请输入角色编码');
 return;
 }
 const data = {
 ...form,
 status: parseInt(form.status),
 sortOrder: parseInt(form.sortOrder) || 0
 };
 if (isAdd.value) {
 addRole(data).then(res => {
 if (res.code === 200) {
 ElMessage.success('添加成功');
 showModal.value = false;
 loadData();
 }
 });
 }
 else {
 updateRole(data).then(res => {
 if (res.code === 200) {
 ElMessage.success('更新成功');
 showModal.value = false;
 loadData();
 }
 });
 }
};
const handleDelete = (row) => {
 ElMessage.confirm('确定要删除该角色吗？', '提示', {
 confirmButtonText: '确定',
 cancelButtonText: '取消'
 }).then(() => {
 deleteRole(row.id).then(res => {
 if (res.code === 200) {
 ElMessage.success('删除成功');
 loadData();
 }
 });
 });
};
loadData();
</script>