// API Adresi
const API_BASE_URL = 'http://localhost:8080/api/personeller';
const DEPARTMAN_API_URL = 'http://localhost:8080/api/departmanlar';
const IZIN_TURU_API_URL = 'http://localhost:8080/api/izin-turleri';


// Sayfalar (Bölümler)
const homePage = document.getElementById('homePage');
const detailPage = document.getElementById('detailPage');
const updatePage = document.getElementById('updatePage');
const addPage = document.getElementById('addPage');
const leavePage = document.getElementById('leavePage');
const leaveListPage = document.getElementById('leaveListPage');
const leaveUpdatePage = document.getElementById('leaveUpdatePage');

// Elemanlar
const personelTableBody = document.getElementById('personelTableBody');
const statusMessage = document.getElementById('statusMessage');
const customContextMenu = document.getElementById('customContextMenu');
const themeToggleBtn = document.getElementById('themeToggle');
const izinListesiBody = document.getElementById('izinListesiBody');

// Excel Elemanları
const excelFile = document.getElementById('excelFile');
const btnExcelSec = document.getElementById('btnExcelSec');
const btnExcelIndir = document.getElementById('btnExcelIndir');

// Formlar
const personelForm = document.getElementById('personelForm');
const updateForm = document.getElementById('updateForm');
const leaveForm = document.getElementById('leaveForm');
const leaveUpdateForm = document.getElementById('leaveUpdateForm');
const IZIN_API_URL = 'http://localhost:8080/api/izinler';

// Hafızada tutulacak geçici değişkenler
let selectedPersonelId = null;
let activePersonelData = []; // Tüm personellerin listesi buraya saklanacak

let currentLeavePersonelId = null; // İzin listesini yenilerken kimin listesi olduğunu hatırlamak için
let currentIzinListData = []; // Silme ve Güncelleme için verileri hafızada tutarız

// ─── SAYFA GEÇİŞ YÖNETİMİ (SPA) ───
function showView(targetView) {
    [homePage, detailPage, updatePage, addPage,leavePage, leaveListPage, leaveUpdatePage].forEach(view => {
        view.classList.add('hidden');
    });
    targetView.classList.remove('hidden');
}

// ─── BİLDİRİM MESAJI GÖSTERME ───
function showMessage(text, isSuccess) {
    statusMessage.textContent = text;
    statusMessage.className = `message ${isSuccess ? 'success' : 'error'}`;
    statusMessage.classList.remove('hidden');
    setTimeout(() => {
        statusMessage.classList.add('hidden');
    }, 5000);
}

// ─── VERİLERİ BACKEND'DEN ÇEKME ───
function tumPersonelleriGetir() {
    // Normalde backend'de tümünü çeken GET ucunuzu (/api/personeller/excel-indir gibi)
    // Excel olarak tasarladınız. Ancak ana sayfa listesi için veritabanındaki verileri
    // anlık okuyabilmek için, bu fetch bloğu Spring'in /api/personeller/liste (veya benzeri)
    // endpoint'iyle konuşur. Spring'de List<Personel> dönen bir GET ucu yoksa projenizde,
    // lütfen Controller'a ek bir liste GET ucu yazmayı unutmayın.
    fetch(`${API_BASE_URL}/liste`)
        .then(res => {
            if (!res.ok) throw new Error("Veriler yüklenemedi!");
            return res.json();
        })
        .then(data => {
            activePersonelData = data;
            personelTablosunuDoldur(data);
        })
        .catch(err => {
            console.error("Tablo yükleme hatası:", err);
            // Eğer backend'de "/liste" ucu henüz yoksa, kullanıcıya uyarı gösteriyoruz:
            personelTableBody.innerHTML = `<tr><td colspan="4" style="text-align: center; color: red;">Personel verileri çekilemedi. Backend tarafına tüm personelleri çeken '/api/personeller/liste' GET ucunu eklemelisiniz!</td></tr>`;
        });
}


// ─── DEPARTMANLARI BACKEND'DEN ÇEK VE KUTULARA DOLDUR ───
function departmanlariGetir() {
    fetch(DEPARTMAN_API_URL)
        .then(res => res.json())
        .then(data => {
            let optionsHTML = '<option value="">-- Lütfen Departman Seçin --</option>';
            data.forEach(d => {
                optionsHTML += `<option value="${d.id}">${d.ad}</option>`;
            });
            // Hem ekleme hem de güncelleme formundaki select kutularını doldur
            document.getElementById('departman_id').innerHTML = optionsHTML;
            document.getElementById('update-departman_id').innerHTML = optionsHTML;
        })
        .catch(err => console.error("Departmanlar çekilemedi:", err));
}


// ─── İZİN TÜRLERİNİ BACKEND'DEN ÇEK ───
function izinTurleriniGetir() {
    fetch(IZIN_TURU_API_URL)
        .then(res => res.json())
        .then(data => {
            let optionsHTML = '<option value="">-- İzin Türü Seçin --</option>';
            data.forEach(d => {
                optionsHTML += `<option value="${d.id}">${d.ad}</option>`;
            });
            document.getElementById('izin-turu').innerHTML = optionsHTML;
        })
        .catch(err => console.error("İzin türleri çekilemedi:", err));
}


// ─── TABLOYU DOLDURMA ───
function personelTablosunuDoldur(personeller) {
    personelTableBody.innerHTML = '';

    if (personeller.length === 0) {
        personelTableBody.innerHTML = '<tr><td colspan="6" style="text-align: center;">Kayıtlı personel bulunamadı.</td></tr>';
        return;
    }

    personeller.forEach(p => {
        const tr = document.createElement('tr');
        tr.style.borderBottom = "1px solid var(--border-color, #eee)";
        tr.dataset.id = p.id;

        // Tarih formatı düzenleme
        const sonGuncelleme = p.kartSonGuncelleme ? new Date(p.kartSonGuncelleme).toLocaleString('tr-TR') : '-';

        //Departman kontrolü (Güvenli zincirleme kontrolü)
        // Eğer personel'in departmanı varsa ve departmanın adı varsa onu yaz, yoksa "Atanmamış" yaz.
        const departmanAdi = (p.departman && p.departman.ad) ? p.departman.ad : '<i>Atanmamış</i>';


        tr.innerHTML = `
    <td>${p.tckn}</td>
    <td>${p.ad}</td>
    <td>${p.soyad}</td>
    <td>${departmanAdi}</td>
    <td>${p.email || '-'}</td>
    <td>${p.telefon || '-'}</td>
    <td>${sonGuncelleme}</td>
`;



        // 1. Sol Tıklama: Detay Sayfası
        tr.addEventListener('click', (e) => {
            detaySayfasiAc(p.id);
        });

        // 2. Sağ Tıklama: Özel Menü
        tr.addEventListener('contextmenu', (e) => {
            e.preventDefault(); // Varsayılan tarayıcı menüsünü engelle
            selectedPersonelId = p.id;
            sagTikMenusuGoster(e.clientX, e.clientY);
        });

        personelTableBody.appendChild(tr);
    });
}

// ─── DETAY SAYFASI AÇMA (SOL TIK) ───
function detaySayfasiAc(id) {
    const p = activePersonelData.find(item => item.id === id);
    if (!p) return;

    document.getElementById('detay-id').textContent = p.id;
    document.getElementById('detay-tckn').textContent = p.tckn;
    document.getElementById('detay-ad').textContent = p.ad;
    document.getElementById('detay-soyad').textContent = p.soyad;
    const departmanAdi = (p.departman && p.departman.ad) ? p.departman.ad : 'Atanmamış';
    document.getElementById('detay-departman').textContent = departmanAdi;
    document.getElementById('detay-email').textContent = p.email || '-';
    document.getElementById('detay-yas').textContent = p.yas || '-';
    document.getElementById('detay-telefon').textContent = p.telefon || '-';
    document.getElementById('detay-maas').textContent = p.maas ? `${p.maas} ₺` : '-';
    document.getElementById('detay-iseGiris').textContent = p.iseGirisTarihi || '-';

    showView(detailPage);
}

// ─── SAĞ TIK MENÜSÜ GÖSTERİMİ ───
function sagTikMenusuGoster(x, y) {
    customContextMenu.style.left = `${x}px`;
    customContextMenu.style.top = `${y}px`;
    customContextMenu.classList.remove('hidden');
}

// Menüyü kapatma
window.addEventListener('click', () => {
    customContextMenu.classList.add('hidden');
});

// ─── MENÜ EYLEMLERİ ───


// İZİN EKLE Tıklanınca
document.getElementById('menuIzin').addEventListener('click', () => {
    const p = activePersonelData.find(item => item.id === selectedPersonelId);
    if (!p) return;

    // Hangi personele izin eklediğimizi başlıkta gösterelim
    document.getElementById('izin-personel-isim').textContent = `${p.ad} ${p.soyad}`;

    leaveForm.reset(); // Formu temizle
    showView(leavePage); // İzin sayfasını aç
});



// GÜNCELLE Tıklanınca
document.getElementById('menuGuncelle').addEventListener('click', () => {
    const p = activePersonelData.find(item => item.id === selectedPersonelId);
    if (!p) return;

    // Güncelleme formunu doldur
    document.getElementById('update-id').value = p.id;
    document.getElementById('update-tckn').value = p.tckn;
    document.getElementById('update-ad').value = p.ad;
    document.getElementById('update-soyad').value = p.soyad;
    document.getElementById('update-email').value = p.email || '';
    document.getElementById('update-telefon').value = p.telefon || '';
    document.getElementById('update-yas').value = p.yas || '';
    document.getElementById('update-maas').value = p.maas || '';
    document.getElementById('update-iseGiris').value = p.iseGirisTarihi || '';
    // YENİ: Personelin mevcut departmanı varsa onu seçili hale getir
    if (p.departman && p.departman.id) {
        document.getElementById('update-departman_id').value = p.departman.id;
    } else {
        document.getElementById('update-departman_id').value = '';
    }

    showView(updatePage);
});

// SİL Tıklanınca
document.getElementById('menuSil').addEventListener('click', () => {
    if (confirm('Bu personeli silmek istediğinize emin misiniz?')) {
        // Backend'deki deleteById metoduna DELETE isteği gönderiyoruz
        fetch(`${API_BASE_URL}/sil/${selectedPersonelId}`, {
            method: 'DELETE'
        })
            .then(async res => {
                if (res.ok) {
                    showMessage("Personel başarıyla silindi.", true);
                    tumPersonelleriGetir(); // Tabloyu yenile
                } else {
                    const errText = await res.text();
                    showMessage(`Silme hatası: ${errText}`, false);
                }
            })
            .catch(err => {
                showMessage("Bağlantı hatası oluştu!", false);
                console.error(err);
            });
    }
});



// ─── İZİNLERİ GÖSTER MENÜSÜNE TIKLANINCA ───
document.getElementById('menuIzinListe').addEventListener('click', () => {
    const p = activePersonelData.find(item => item.id === selectedPersonelId);
    if (!p) return;

    currentLeavePersonelId = p.id; // Personeli hafızaya al
    document.getElementById('izin-liste-personel-isim').textContent = `${p.ad} ${p.soyad}`;

    izinleriGetirVeCiz(currentLeavePersonelId);
    showView(leaveListPage);
});

// GET /api/personeller/{id}/izinler İsteği
function izinleriGetirVeCiz(personelId) {
    fetch(`http://localhost:8080/api/personeller/${personelId}/izinler`)
        .then(res => res.json())
        .then(data => {
            currentIzinListData = data; // Veriyi global hafızaya al (Düzenle butonu için lazım)
            izinListesiBody.innerHTML = ''; // Tabloyu temizle

            if (data.length === 0) {
                izinListesiBody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Bu personelin kayıtlı izni bulunmuyor.</td></tr>';
                return;
            }

            data.forEach(izin => {
                const turAdi = (izin.izinTuru && izin.izinTuru.ad) ? izin.izinTuru.ad : '-';
                const aciklama = izin.izinAciklamasi || '-';

                const tr = document.createElement('tr');


                tr.innerHTML = `
                    <td style="padding: 12px 15px;">${izin.baslangicTarihi}</td>
                    <td style="padding: 12px 15px;">${izin.bitisTarihi}</td>
                    <td style="padding: 12px 15px;">${turAdi}</td>
                    <td style="padding: 12px 15px;">${aciklama}</td>
                    <td style="padding: 12px 15px;">
                        <button onclick="izinDuzenleEkraniniAc('${izin.id}')" class="btn btn-small" style="background-color: var(--primary-color); color: white; padding: 5px 10px; border-radius: 4px; border:none; cursor:pointer;">✏️ Düzenle</button>
                        <button onclick="izinSil('${izin.id}')" class="btn btn-small" style="background-color: var(--danger-color); color: white; padding: 5px 10px; border-radius: 4px; border:none; cursor:pointer; margin-left: 5px;">❌ Sil</button>
                    </td>
                `;


                izinListesiBody.appendChild(tr);
            });
        })
        .catch(err => showMessage("İzinler getirilirken hata oluştu!", false));
}



// ─── İZİN SİLME İŞLEMİ ───
window.izinSil = function(izinId) {
    if (!confirm("Bu izin kaydını tamamen silmek istediğinize emin misiniz?")) return;

    fetch(`http://localhost:8080/api/izinler/${izinId}`, {
        method: 'DELETE'
    })
        .then(res => {
            if (res.ok) {
                showMessage("İzin başarıyla silindi!", true);
                izinleriGetirVeCiz(currentLeavePersonelId); // Tabloyu anında yenile
            } else {
                showMessage("Silme işlemi başarısız oldu.", false);
            }
        })
        .catch(err => showMessage("Sunucuya bağlanılamadı!", false));
}

// ─── İZİN DÜZENLE EKRANINI AÇMA ───
window.izinDuzenleEkraniniAc = function(izinId) {
    const izin = currentIzinListData.find(i => i.id === izinId);
    if (!izin) return;

    // Formu doldur
    document.getElementById('guncelle-izin-id').value = izin.id;
    document.getElementById('guncelle-izin-baslangic').value = izin.baslangicTarihi;
    document.getElementById('guncelle-izin-bitis').value = izin.bitisTarihi;
    document.getElementById('guncelle-izin-aciklamasi').value = izin.izinAciklamasi || '';

    // İzin türleri menüsünü diğer formdan (izin-turu) birebir kopyalayıp içine koyuyoruz
    const guncelleTurSelect = document.getElementById('guncelle-izin-turu');
    guncelleTurSelect.innerHTML = document.getElementById('izin-turu').innerHTML;

    if (izin.izinTuru && izin.izinTuru.id) {
        guncelleTurSelect.value = izin.izinTuru.id;
    }

    showView(leaveUpdatePage);
}




// ─── GÜNCELLEME FORMU KAYDETME (SUBMIT) ───
updateForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const personelId = document.getElementById('update-id').value;

    const emailVal = document.getElementById('update-email').value;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailVal)) {
        showMessage('Hata: Lütfen geçerli bir e-posta adresi giriniz!', false);
        return;
    }

    const data = {
        tckn: document.getElementById('update-tckn').value,
        ad: document.getElementById('update-ad').value,
        soyad: document.getElementById('update-soyad').value,
        email: emailVal,
        telefon: document.getElementById('update-telefon').value || null,
        yas: document.getElementById('update-yas').value ? parseInt(document.getElementById('update-yas').value) : null,
        maas: document.getElementById('update-maas').value ? parseFloat(document.getElementById('update-maas').value) : null,
        iseGirisTarihi: document.getElementById('update-iseGiris').value,
        departman: {
            id: parseInt(document.getElementById('update-departman_id').value)
        }
    };

    // Güncelleme için de backend'deki /kaydet POST ucunu kullanıyoruz (çünkü kodunuzda TCKN varsa güncelliyor)
    fetch(`${API_BASE_URL}/${personelId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(async res => {
            const text = await res.text();
            if (res.ok) {
                showMessage(text, true);
                showView(homePage);
                tumPersonelleriGetir(); // Tabloyu tazele
            } else {
                showMessage(text, false);
            }
        })
        .catch(err => showMessage("Güncelleme sırasında hata oluştu!", false));
});

// ─── YENİ EKLEME FORMU KAYDETME (TC UYARILI) ───
personelForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const submitBtn = e.target.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Kaydediliyor...';

    const tcknVal = document.getElementById('tckn').value;

    // 🔍 TC Kimlik No - Sadece Rakam Kontrolü ve Uzunluk Kontrolü (İstediğin Kritik Regex Kodu)
    if (!/^\d+$/.test(tcknVal)) {
        showMessage('Hata: TCKN sadece rakamlardan oluşmalıdır!', false);
        return;
    }

    const emailVal = document.getElementById('email').value;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailVal)) {
        showMessage('Hata: Lütfen geçerli bir e-posta adresi giriniz!', false);
        return;
    }

    const data = {
        tckn: tcknVal,
        ad: document.getElementById('ad').value,
        soyad: document.getElementById('soyad').value,
        email: emailVal,
        telefon: document.getElementById('telefon').value || null,
        yas: document.getElementById('yas').value ? parseInt(document.getElementById('yas').value) : null,
        maas: document.getElementById('maas').value ? parseFloat(document.getElementById('maas').value) : null,
        iseGirisTarihi: document.getElementById('iseGirisTarihi').value,
        departman: {
            id: parseInt(document.getElementById('departman_id').value)
        }
    };

    fetch(`${API_BASE_URL}`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    })
        .then(async res => {
            const text = await res.text();
            if (res.ok) {
                showMessage(text, true);
                personelForm.reset();
                showView(homePage);
                tumPersonelleriGetir();
            } else {
                showMessage(text, false);
            }
        })
        .catch(err => {
            showMessage("Kayıt sırasında bağlantı hatası oluştu!", false);
        })
        .finally(() => {
            // 2. İŞLEM BİTİNCE BUTONU TEKRAR AKTİF ET (Hata olsa bile kilitli kalmasın)
            submitBtn.disabled = false;
            submitBtn.style.opacity = "1";
            submitBtn.textContent = 'Kaydet / Yeni Ekle';
        });
});


// ─── İZİN FORMU KAYDETME (POST) VE VALİDASYON ───
leaveForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const baslangic = document.getElementById('izin-baslangic').value;
    const bitis = document.getElementById('izin-bitis').value;
    const tur = document.getElementById('izin-turu').value;

    // 🔍 TARİH VALİDASYONU: Bitiş tarihi başlangıçtan önce olamaz!
    if (new Date(bitis) < new Date(baslangic)) {
        showMessage('Hata: Bitiş tarihi, başlangıç tarihinden önce olamaz!', false);
        return; // İşlemi durdur
    }

    const data = {
        baslangicTarihi: baslangic,
        bitisTarihi: bitis,
        izinTuru: {
            id: document.getElementById('izin-turu').value
        },
        izinAciklamasi: document.getElementById('izin-aciklamasi').value || null,
        personel: {
            id: selectedPersonelId // Sağ tıklanan personelin ID'sini yolluyoruz
        }
    };

    fetch(IZIN_API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(async res => {
            if (res.ok) {
                showMessage("İzin başarıyla eklendi!", true);
                leaveForm.reset();
                showView(homePage);
            } else {
                const text = await res.text();
                showMessage("Hata: " + text, false);
            }
        })
        .catch(err => showMessage("İzin kaydedilirken bağlantı hatası oluştu!", false));
});




// ─── İZİN GÜNCELLEME (PUT) VE VALİDASYON ───
leaveUpdateForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const id = document.getElementById('guncelle-izin-id').value;
    const baslangic = document.getElementById('guncelle-izin-baslangic').value;
    const bitis = document.getElementById('guncelle-izin-bitis').value;

    // Tarih validasyonu
    if (new Date(bitis) < new Date(baslangic)) {
        showMessage('Hata: Bitiş tarihi, başlangıç tarihinden önce olamaz!', false);
        return;
    }

    const data = {
        baslangicTarihi: baslangic,
        bitisTarihi: bitis,
        izinTuru: {
            id: document.getElementById('guncelle-izin-turu').value
        },
        izinAciklamasi: document.getElementById('guncelle-izin-aciklamasi').value || null,
        personel: {
            id: currentLeavePersonelId // Hangi personele ait olduğu
        }
    };

    fetch(`http://localhost:8080/api/izinler/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(async res => {
            if (res.ok) {
                showMessage("İzin başarıyla güncellendi!", true);
                izinleriGetirVeCiz(currentLeavePersonelId); // Verileri yenile
                showView(leaveListPage); // Listeye geri dön
            } else {
                const text = await res.text();
                showMessage("Hata: " + text, false);
            }
        })
        .catch(err => showMessage("Güncellenirken bağlantı hatası oluştu!", false));
});

// Güncelleme ekranından İzin Listesine geri dönme butonu
document.getElementById('btn-back-to-leave-list').addEventListener('click', () => {
    showView(leaveListPage);
});





// ─── GERİ DÖNÜŞ BUTONLARI (GLOBAL KONTROL) ───
document.querySelectorAll('.btn-back').forEach(btn => {
    btn.addEventListener('click', () => {
        showView(homePage);
    });
});

// ─── NAVİGASYON: YENİ KİŞİ EKLE BUTONU ───
document.getElementById('btnYeniEkle').addEventListener('click', () => {
    showView(addPage);
});

// ─── EXCEL ENTEGRASYONLARI ───

// 📤 Excel Yükle Butonuna Tıklanınca Klasik Dosya Seçim Kutusunu Tetikleme
btnExcelSec.addEventListener('click', () => {
    excelFile.click();
});

// Dosya seçilince otomatik yükleme tetiklenmesi
excelFile.addEventListener('change', () => {
    const file = excelFile.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    btnExcelSec.disabled = true;
    btnExcelSec.textContent = 'Yükleniyor...';

    fetch(`${API_BASE_URL}/excel-yukle`, {
        method: 'POST',
        body: formData
    })
        .then(async res => {
            const text = await res.text();
            if (res.ok) {
                showMessage(text, true);
                tumPersonelleriGetir(); // Yeni yüklenenleri anında tabloya getir
            } else {
                showMessage(text, false);
            }
        })
        .catch(err => showMessage("Bağlantı hatası oluştu!", false))
        .finally(() => {
            btnExcelSec.disabled = false;
            btnExcelSec.textContent = '📤 Excel Yükle';
            excelFile.value = ''; // Seçimi temizle
        });
});

// 📥 Excel İndir (Export) - Doğrudan tetikler
btnExcelIndir.addEventListener('click', () => {
    window.location.href = `${API_BASE_URL}/excel-indir`;
});

// ─── GECE / GÜNDÜZ MODU ───
const savedTheme = localStorage.getItem('theme');
if (savedTheme === 'dark') {
    document.body.classList.add('dark-mode');
    themeToggleBtn.textContent = '☀️ Gündüz Modu';
}

themeToggleBtn.addEventListener('click', () => {
    document.body.classList.toggle('dark-mode');
    if (document.body.classList.contains('dark-mode')) {
        themeToggleBtn.textContent = '☀️ Gündüz Modu';
        localStorage.setItem('theme', 'dark');
    } else {
        themeToggleBtn.textContent = '🌙 Gece Modu';
        localStorage.setItem('theme', 'light');
    }
});

// Sayfa ilk açıldığında verileri veritabanından çekelim
document.addEventListener('DOMContentLoaded', () => {
    tumPersonelleriGetir();
    departmanlariGetir();
    izinTurleriniGetir();
});


