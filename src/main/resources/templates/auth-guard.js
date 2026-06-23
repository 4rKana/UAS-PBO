
(function () {
    
    const idUser = localStorage.getItem("idUser");

    
    if (!idUser) {
        alert("Akses ditolak! Anda harus login terlebih dahulu.");
        
        
        window.location.href = "login.html";
    }
})();