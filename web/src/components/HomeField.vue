<template>
    <div class="container home-field">
      <div class="card" style="background-color: rgba(255,0,0,0);border-style: none;" >
        <div class="card-body">
          <slot></slot>
        </div>
      </div>
    </div>
  </template>

  <script>
  
  </script>
  
  <style scoped>
  
  div.container{
          margin-top: 30vh;
  }
  div.content-field{
          margin-top: 15px;
  }

  @media (max-width: 932px) {
    div.container{
      margin-top: 8vh;
    }
    div.card {
      width: 100vw;
    }
  }
  
  </style>