<script lang="ts">
  let count: number = $state(0)
  const increment = () => {
    count += 1
  }
</script>

<button 
  onclick={increment}
  class="bg-slate-600 font-xl rounded text-white hover:bg-slate-400"
>
  count is {count}
</button>
