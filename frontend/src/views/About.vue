<template>
  <div class="about-page">
    <div class="page-banner">
      <h1>关于我们</h1>
      <p>用心服务，让每一位老人安享幸福晚年</p>
    </div>

    <div class="about-container">
      <section class="about-section">
        <h2>机构简介</h2>
        <p>
          暖夕伴养老服务中心成立于2020年，是一家专注于为老年人提供全方位、高品质养老服务的专业机构。
          我们秉承"用心服务、温暖相伴"的理念，通过智慧化手段与传统养老服务深度融合，为社区老年人
          提供助餐、保洁、护理、康复、陪伴等多元化服务，让老年人在熟悉的环境中安享幸福晚年。
        </p>
        <p>
          中心现有专业护理团队50余人，均持有国家认证的护理资格证书，服务覆盖周边10个社区，
          累计服务老年人超过3000人次，客户满意度达98%以上。我们始终坚持"以人为本"的服务宗旨，
          不断优化服务流程，提升服务质量，努力打造暖夕伴养老行业标杆。
        </p>
      </section>

      <section class="stats-section">
        <div class="stats-grid">
          <div class="stat-card" style="background:linear-gradient(135deg,#FF8C00,#FFA940)">
            <div class="stat-number">3000+</div>
            <div class="stat-label">服务老人数</div>
          </div>
          <div class="stat-card" style="background:linear-gradient(135deg,#52C41A,#73D13D)">
            <div class="stat-number">50+</div>
            <div class="stat-label">专业护理员</div>
          </div>
          <div class="stat-card" style="background:linear-gradient(135deg,#1890FF,#40A9FF)">
            <div class="stat-number">10</div>
            <div class="stat-label">覆盖社区</div>
          </div>
          <div class="stat-card" style="background:linear-gradient(135deg,#722ED1,#9254DE)">
            <div class="stat-number">98%</div>
            <div class="stat-label">客户满意度</div>
          </div>
        </div>
      </section>

      <section class="about-section">
        <h2>服务理念</h2>
        <div class="philosophy-grid">
          <div class="philosophy-card">
            <el-icon :size="48" color="#FF8C00"><Checked /></el-icon>
            <h3>用心服务</h3>
            <p>以老人需求为中心，提供贴心、周到的服务</p>
          </div>
          <div class="philosophy-card">
            <el-icon :size="48" color="#52C41A"><Shield /></el-icon>
            <h3>安全第一</h3>
            <p>严格的安全管理体系，保障老人人身安全</p>
          </div>
          <div class="philosophy-card">
            <el-icon :size="48" color="#1890FF"><Connection /></el-icon>
            <h3>智慧赋能</h3>
            <p>运用科技手段，提升服务效率和质量</p>
          </div>
          <div class="philosophy-card">
            <el-icon :size="48" color="#722ED1"><Star /></el-icon>
            <h3>持续改进</h3>
            <p>不断优化服务流程，追求卓越品质</p>
          </div>
        </div>
      </section>

      <section class="about-section">
        <h2>联系我们</h2>
        <div class="contact-grid">
          <div class="contact-item">
            <el-icon :size="32" color="#FF8C00"><Phone /></el-icon>
            <div>
              <h4>服务热线</h4>
              <p>400-888-9999</p>
            </div>
          </div>
          <div class="contact-item">
            <el-icon :size="32" color="#52C41A"><Clock /></el-icon>
            <div>
              <h4>服务时间</h4>
              <p>周一至周日 8:00-20:00</p>
            </div>
          </div>
          <div class="contact-item">
            <el-icon :size="32" color="#1890FF"><Location /></el-icon>
            <div>
              <h4>中心地址</h4>
              <p>幸福路100号养老服务大厦</p>
            </div>
          </div>
          <div class="contact-item">
            <el-icon :size="32" color="#722ED1"><Message /></el-icon>
            <div>
              <h4>电子邮箱</h4>
              <p>service@smart-elderly.com</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #722ED1, #9254DE);
  padding: 60px 24px;
  text-align: center;
  color: #fff;
  h1 { font-size: 2.2rem; font-weight: 700; margin-bottom: 12px; }
  p { font-size: 1.2rem; opacity: 0.9; }
}

.about-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 24px;
}

.about-section {
  background: #fff;
  border-radius: 16px;
  padding: 36px;
  margin-bottom: 32px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  h2 {
    font-size: 1.5rem;
    color: #333;
    margin-bottom: 20px;
    padding-left: 16px;
    border-left: 4px solid #FF8C00;
  }
  p {
    font-size: 1.05rem;
    line-height: 2;
    color: #666;
    margin-bottom: 16px;
  }
}

.stats-section { margin-bottom: 32px; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.stat-card {
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  color: #fff;
  .stat-number { font-size: 2.5rem; font-weight: 700; margin-bottom: 8px; }
  .stat-label { font-size: 1rem; opacity: 0.9; }
}

.philosophy-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.philosophy-card {
  text-align: center;
  padding: 32px;
  border-radius: 12px;
  background: #FFF8F0;
  h3 { font-size: 1.2rem; color: #333; margin: 16px 0 8px; }
  p { color: #888; font-size: 0.95rem; margin-bottom: 0; }
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  background: #FFF8F0;
  h4 { font-size: 1rem; color: #333; margin-bottom: 4px; }
  p { color: #888; font-size: 0.95rem; margin-bottom: 0; }
}

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .philosophy-grid, .contact-grid { grid-template-columns: 1fr; }
}
</style>
